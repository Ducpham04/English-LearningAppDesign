import { API_BASE_URL } from '@/services/baseUrl';

export type RealtimeRole = 'user' | 'assistant';

export type RealtimeClientState =
  | 'idle'
  | 'connecting'
  | 'listening'
  | 'ai_speaking'
  | 'error';

export interface RealtimeTranscriptEvent {
  role: RealtimeRole;
  text: string;
  isDelta: boolean;
  isFinal: boolean;
}

export interface RealtimeClientEvents {
  onState?: (state: RealtimeClientState) => void;
  onTranscript?: (evt: RealtimeTranscriptEvent) => void;
  onError?: (message: string) => void;
  onRawEvent?: (evt: any) => void;
}

interface StartOptions {
  /** Optional extra instructions (e.g., lesson context) */
  instructions?: string;
  /** ws(s) override. If omitted, will derive from API_BASE_URL */
  wsUrl?: string;
}

/**
 * Browser realtime speech client:
 * - Captures mic -> resamples to PCM16 16k mono -> streams via WebSocket (binary frames)
 * - Receives PCM16 audio from server and plays it
 * - Uses server-side VAD (OpenAI) to detect end of speech; server triggers response automatically.
 */
export class RealtimeSpeechClient {
  private ws: WebSocket | null = null;
  private state: RealtimeClientState = 'idle';
  private events: RealtimeClientEvents;

  private micStream: MediaStream | null = null;
  private audioCtx: AudioContext | null = null;
  private micSource: MediaStreamAudioSourceNode | null = null;
  private micProcessor: ScriptProcessorNode | null = null;

  private listeningEnabled = true;
  private outputSampleRate = 24000;
  private nextPlayTime = 0;
  private closedByUser = false;

  constructor(events: RealtimeClientEvents = {}) {
    this.events = events;
  }

  getState() {
    return this.state;
  }

  setListeningEnabled(enabled: boolean) {
    this.listeningEnabled = enabled;
  }

  /** Send a raw Realtime event to backend (will be forwarded to OpenAI). */
  sendEvent(evt: any) {
    this.sendJson(evt);
  }

  async start(opts: StartOptions = {}) {
    if (this.ws && (this.ws.readyState === WebSocket.OPEN || this.ws.readyState === WebSocket.CONNECTING)) {
      return;
    }
    this.closedByUser = false;
    this.setState('connecting');

    const wsUrl = opts.wsUrl ?? deriveWsUrlFromApiBase(API_BASE_URL);
    this.ws = new WebSocket(wsUrl);
    this.ws.binaryType = 'arraybuffer';

    this.ws.onopen = async () => {
      try {
        // Optionally update instructions per lesson/context (backend forwards to OpenAI).
        if (opts.instructions && opts.instructions.trim()) {
          this.sendJson({
            type: 'session.update',
            session: { instructions: opts.instructions.trim() },
          });
        }

        await this.initAudio();
        this.setState('listening');
      } catch (e: any) {
        this.fail(e?.message || 'Failed to start audio');
      }
    };

    this.ws.onmessage = (ev) => {
      if (typeof ev.data === 'string') {
        this.handleTextEvent(ev.data);
      } else {
        this.handleAudioDelta(ev.data as ArrayBuffer);
      }
    };

    this.ws.onerror = () => {
      if (!this.closedByUser) this.fail('WebSocket error');
    };

    this.ws.onclose = () => {
      this.cleanupAudio();
      if (!this.closedByUser) {
        this.setState('idle');
      }
    };
  }

  stop() {
    this.closedByUser = true;
    this.cleanupAudio();
    if (this.ws) {
      try {
        this.ws.close();
      } catch {
        // ignore
      }
    }
    this.ws = null;
    this.setState('idle');
  }

  private setState(s: RealtimeClientState) {
    if (this.state === s) return;
    this.state = s;
    this.events.onState?.(s);
  }

  private fail(message: string) {
    this.events.onError?.(message);
    this.setState('error');
    this.stop();
  }

  private sendJson(obj: any) {
    if (!this.ws || this.ws.readyState !== WebSocket.OPEN) return;
    this.ws.send(JSON.stringify(obj));
  }

  private async initAudio() {
    // Capture mic
    this.micStream = await navigator.mediaDevices.getUserMedia({
      audio: {
        echoCancellation: true,
        noiseSuppression: true,
        autoGainControl: true,
      },
    });

    // Playback + capture in one AudioContext
    this.audioCtx = new (window.AudioContext || (window as any).webkitAudioContext)();
    this.nextPlayTime = this.audioCtx.currentTime;

    this.micSource = this.audioCtx.createMediaStreamSource(this.micStream);
    // ScriptProcessorNode is deprecated but widely supported; good enough for this app.
    this.micProcessor = this.audioCtx.createScriptProcessor(4096, 1, 1);

    const inputSampleRate = this.audioCtx.sampleRate;
    this.micProcessor.onaudioprocess = (e) => {
      if (!this.listeningEnabled) return;
      if (!this.ws || this.ws.readyState !== WebSocket.OPEN) return;

      // If AI is speaking, pause sending mic to avoid echo.
      if (this.state === 'ai_speaking') return;

      const input = e.inputBuffer.getChannelData(0);
      const pcm16 = floatToPcm16(resampleFloat32(input, inputSampleRate, 16000));
      this.ws.send(pcm16.buffer);
    };

    this.micSource.connect(this.micProcessor);
    // Avoid local monitoring feedback: connect to a zero-gain node.
    const zeroGain = this.audioCtx.createGain();
    zeroGain.gain.value = 0;
    this.micProcessor.connect(zeroGain);
    zeroGain.connect(this.audioCtx.destination);
  }

  private cleanupAudio() {
    try {
      this.micProcessor?.disconnect();
      this.micSource?.disconnect();
    } catch {
      // ignore
    }
    this.micProcessor = null;
    this.micSource = null;

    if (this.micStream) {
      this.micStream.getTracks().forEach((t) => t.stop());
      this.micStream = null;
    }

    if (this.audioCtx) {
      try {
        this.audioCtx.close();
      } catch {
        // ignore
      }
      this.audioCtx = null;
    }
  }

  private handleTextEvent(text: string) {
    try {
      const evt = JSON.parse(text);
      this.events.onRawEvent?.(evt);

      const type = String(evt?.type || '');
      if (type === 'response.created') {
        this.setState('ai_speaking');
      } else if (type === 'response.done') {
        // Response finished -> resume listening
        this.setState('listening');
      } else if (type === 'conversation.item.input_audio_transcription.completed') {
        const t = String(evt?.transcript || evt?.text || evt?.item?.content?.[0]?.transcript || '').trim();
        if (t) {
          this.events.onTranscript?.({ role: 'user', text: t, isDelta: false, isFinal: true });
        }
      } else if (type === 'response.audio_transcript.delta') {
        const delta = String(evt?.delta || '').trim();
        if (delta) {
          this.events.onTranscript?.({ role: 'assistant', text: delta, isDelta: true, isFinal: false });
        }
      } else if (type === 'error') {
        const msg = String(evt?.error?.message || 'Realtime error');
        this.fail(msg);
      }
    } catch {
      // ignore parse errors
    }
  }

  private handleAudioDelta(buf: ArrayBuffer) {
    if (!this.audioCtx) return;
    // Server forwards raw PCM16 bytes (little endian) at ~24kHz
    const float32 = pcm16ToFloat32(new Int16Array(buf));
    playPcmFloat32(this.audioCtx, float32, this.outputSampleRate, (t) => {
      this.nextPlayTime = t;
    }, this.nextPlayTime);
  }
}

function deriveWsUrlFromApiBase(apiBase: string): string {
  // apiBase examples:
  // - '/api'
  // - 'http://localhost:8080/api'
  // - 'https://domain.com/api'
  const path = '/v1/speaking/realtime';
  if (apiBase.startsWith('http://')) {
    return apiBase.replace('http://', 'ws://') + path;
  }
  if (apiBase.startsWith('https://')) {
    return apiBase.replace('https://', 'wss://') + path;
  }
  // Relative -> assume same origin
  const proto = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
  return `${proto}//${window.location.host}${apiBase}${path}`;
}

function resampleFloat32(input: Float32Array, inRate: number, outRate: number): Float32Array {
  if (inRate === outRate) return input;
  const ratio = inRate / outRate;
  const outLen = Math.floor(input.length / ratio);
  const out = new Float32Array(outLen);
  for (let i = 0; i < outLen; i++) {
    const pos = i * ratio;
    const i0 = Math.floor(pos);
    const i1 = Math.min(i0 + 1, input.length - 1);
    const frac = pos - i0;
    out[i] = input[i0] * (1 - frac) + input[i1] * frac;
  }
  return out;
}

function floatToPcm16(input: Float32Array): Int16Array {
  const out = new Int16Array(input.length);
  for (let i = 0; i < input.length; i++) {
    const s = Math.max(-1, Math.min(1, input[i]));
    out[i] = s < 0 ? s * 0x8000 : s * 0x7fff;
  }
  return out;
}

function pcm16ToFloat32(input: Int16Array): Float32Array {
  const out = new Float32Array(input.length);
  for (let i = 0; i < input.length; i++) {
    out[i] = input[i] / 0x8000;
  }
  return out;
}

function playPcmFloat32(
  ctx: AudioContext,
  pcm: Float32Array,
  sampleRate: number,
  setNextTime: (t: number) => void,
  nextTime: number
) {
  const buffer = ctx.createBuffer(1, pcm.length, sampleRate);
  buffer.copyToChannel(pcm, 0);
  const src = ctx.createBufferSource();
  src.buffer = buffer;
  src.connect(ctx.destination);

  const startAt = Math.max(ctx.currentTime, nextTime);
  src.start(startAt);
  setNextTime(startAt + buffer.duration);
}

