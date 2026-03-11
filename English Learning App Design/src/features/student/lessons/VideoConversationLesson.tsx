import { useState, useRef, useEffect, useCallback } from 'react';
import { useNavigate, useParams } from 'react-router';
import { Button } from '@/app/components/ui/button';
import { Card } from '@/app/components/ui/card';
import { Badge } from '@/app/components/ui/badge';
import {
  Play, Pause, RotateCcw, Volume2, VolumeX, CheckCircle2, ArrowLeft,
  AlignLeft, Loader2, ChevronRight, Mic, Video, VideoOff,
} from 'lucide-react';
import { toast } from 'sonner';
import apiService from '@/services/apiService';
import { useAppStore } from '@/store/appStore';
import { RecordingButton } from '@/shared/ui/RecordingButton';
import type { PronunciationScoreResponse } from '@/services/apiService';

interface TranscriptLine {
  sentenceIndex: number;
  start: number;   // ms
  end: number;     // ms
  text: string;
  viText?: string;
  ipa?: string;    // IPA toàn câu, từng từ cách nhau bởi khoảng trắng
}

// Ghép từng từ với IPA tương ứng.
// espeak-ng xuất IPA theo từng từ cách nhau bởi khoảng trắng, nên ta split & ghép theo index.
// Xử lý dấu câu dính vào từ (hello, → hello) để đếm đúng.
function buildWordIpaPairs(text: string, ipa: string): Array<{ word: string; ipa: string }> {
  const rawWords = text.split(/\s+/).filter(Boolean);
  if (!ipa || !ipa.trim()) {
    return rawWords.map(w => ({ word: w, ipa: '' }));
  }
  // Lấy phần chữ của từ (bỏ dấu câu đầu/cuối) để so khớp số lượng với IPA
  const cleanWord = (w: string) => w.replace(/^[^a-zA-Z0-9']+|[^a-zA-Z0-9']+$/g, '');
  const ipaParts = ipa.trim().split(/\s+/).filter(Boolean);
  // Ghép từng từ gốc (có dấu câu) với IPA tương ứng
  let ipaIdx = 0;
  return rawWords.map(word => {
    const clean = cleanWord(word);
    const wordIpa = clean ? (ipaParts[ipaIdx++] ?? '') : '';
    return { word, ipa: wordIpa };
  });
}

function extractYoutubeId(url: string): string | null {
  const m = url.match(/(?:v=|youtu\.be\/|embed\/)([A-Za-z0-9_-]{11})/);
  return m ? m[1] : null;
}

// ─── Toggle Pill ───────────────────────────────────────────────────────────────
function TogglePill({
  active, onClick, label, activeColor,
}: { active: boolean; onClick: () => void; label: string; activeColor: string }) {
  return (
    <button
      onClick={onClick}
      className={`px-3 py-1 rounded-full text-xs font-semibold border transition-all select-none ${
        active
          ? `${activeColor} border-transparent shadow-sm`
          : 'bg-card text-muted-foreground border-border hover:border-muted-foreground/50 hover:text-foreground'
      }`}
    >
      {label}
    </button>
  );
}

/** Điểm tổng từ các chỉ số phát âm (0–100). */
function overallFromResponse(d: PronunciationScoreResponse | null): number {
  if (!d) return 0;
  const p = d.pronunciationScore ?? 0;
  const a = d.accuracyScore ?? 0;
  const f = d.fluencyScore ?? 0;
  const pr = d.prosodyScore ?? 0;
  const c = d.completenessScore ?? 0;
  const arr = [p, a, f, pr, c].filter((x) => x != null && !Number.isNaN(x));
  return arr.length ? Math.round(arr.reduce((s, x) => s + x, 0) / arr.length) : 0;
}

function getScoreColor(score: number) {
  if (score >= 90) return 'text-green-600 dark:text-green-400';
  if (score >= 75) return 'text-yellow-600 dark:text-yellow-400';
  return 'text-red-600 dark:text-red-400';
}

function getScoreBg(score: number) {
  if (score >= 90) return 'bg-green-100 dark:bg-green-900/30';
  if (score >= 75) return 'bg-yellow-100 dark:bg-yellow-900/30';
  return 'bg-red-100 dark:bg-red-900/30';
}

export function VideoConversationLesson() {
  const navigate = useNavigate();
  const { lessonId } = useParams();
  const videoRef = useRef<HTMLVideoElement>(null);
  const scriptRef = useRef<HTMLDivElement>(null);
  const activeLineRef = useRef<HTMLDivElement>(null);
  const ytPlayerRef = useRef<any>(null);
  const pollRef = useRef<ReturnType<typeof setInterval> | null>(null);

  const [lesson, setLesson] = useState<any>(null);
  const [transcript, setTranscript] = useState<TranscriptLine[]>([]);
  const [loading, setLoading] = useState(true);

  // Playback state
  const [isPlaying, setIsPlaying] = useState(false);
  const [isMuted, setIsMuted] = useState(false);
  const [currentMs, setCurrentMs] = useState(0);
  const [duration, setDuration] = useState(0);
  const [activeLineIdx, setActiveLineIdx] = useState(-1);
  const [ytReady, setYtReady] = useState(false);

  // Display toggles – word IPA bật mặc định vì data API đã có sẵn
  const [showScript, setShowScript] = useState(true);
  const [showVI, setShowVI] = useState(true);
  const [showIPA, setShowIPA] = useState(false);      // sentence IPA: tắt mặc định (tránh trùng với word IPA)
  const [showWordIPA, setShowWordIPA] = useState(true); // word IPA: bật mặc định
  const [showOverlaySub, setShowOverlaySub] = useState(true);

  // Thoại theo (shadowing): xem xong → chọn thoại lại từng câu + chấm phát âm
  const [mode, setMode] = useState<'watch' | 'shadowing'>('watch');
  const [showVideoInShadowing, setShowVideoInShadowing] = useState(true);
  const [sentenceScores, setSentenceScores] = useState<Record<number, PronunciationScoreResponse>>({});
  const [recordingForSentenceIndex, setRecordingForSentenceIndex] = useState<number | null>(null);
  const [isRecording, setIsRecording] = useState(false);
  const [recordingTime, setRecordingTime] = useState(0);
  const recordingTimerRef = useRef<NodeJS.Timeout | null>(null);
  const mediaRecorderRef = useRef<MediaRecorder | null>(null);
  const chunksRef = useRef<Blob[]>([]);
  const streamRef = useRef<MediaStream | null>(null);

  const isYoutube = !!(lesson?.youtubeUrl && extractYoutubeId(lesson.youtubeUrl));
  const ytVideoId = isYoutube ? extractYoutubeId(lesson?.youtubeUrl) : null;

  // ── Load lesson ──────────────────────────────────────────────────────────────
  useEffect(() => {
    if (!lessonId) return;
    setLoading(true);
    apiService.lesson.getById(Number(lessonId))
      .then((res) => {
        const data = res.data?.data ?? res.data;
        setLesson(data);
        if (Array.isArray(data?.transcriptJson) && data.transcriptJson.length > 0) {
          setTranscript(data.transcriptJson as TranscriptLine[]);
        } else if (data?.s3Status === 'PROCESSING') {
          pollRef.current = setInterval(async () => {
            try {
              const r = await apiService.transcribe.getTranscript(Number(lessonId));
              const t = r.data?.data ?? r.data;
              if (Array.isArray(t) && t.length > 0) {
                clearInterval(pollRef.current!);
                setTranscript(t as TranscriptLine[]);
                toast.success('Transcript đã sẵn sàng!');
              }
            } catch { /* ignore */ }
          }, 10000);
        }
      })
      .catch(() => toast.error('Không tải được bài học'))
      .finally(() => setLoading(false));
    return () => { if (pollRef.current) clearInterval(pollRef.current); };
  }, [lessonId]);

  // ── Scroll active line into view ─────────────────────────────────────────────
  useEffect(() => {
    activeLineRef.current?.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
  }, [activeLineIdx]);

  // ── Active line detection ────────────────────────────────────────────────────
  const updateActiveLine = useCallback((ms: number) => {
    const idx = transcript.findIndex((l, i) =>
      ms >= l.start && (i === transcript.length - 1 || ms < transcript[i + 1].start)
    );
    setActiveLineIdx(idx);
  }, [transcript]);

  // ── YouTube iframe API ───────────────────────────────────────────────────────
  useEffect(() => {
    if (!ytVideoId) return;
    const init = () => {
      ytPlayerRef.current = new (window as any).YT.Player('yt-player', {
        videoId: ytVideoId,
        playerVars: { rel: 0, modestbranding: 1 },
        events: {
          onReady: () => setYtReady(true),
          onStateChange: (e: any) => setIsPlaying(e.data === 1),
        },
      });
    };
    if ((window as any).YT?.Player) init();
    else {
      if (!(window as any).YT) {
        const tag = document.createElement('script');
        tag.src = 'https://www.youtube.com/iframe_api';
        document.head.appendChild(tag);
      }
      (window as any).onYouTubeIframeAPIReady = init;
    }
  }, [ytVideoId]);

  useEffect(() => {
    if (!ytReady || !ytVideoId) return;
    const id = setInterval(() => {
      if (ytPlayerRef.current?.getCurrentTime) {
        const ms = ytPlayerRef.current.getCurrentTime() * 1000;
        setCurrentMs(ms);
        updateActiveLine(ms);
      }
    }, 200);
    return () => clearInterval(id);
  }, [ytReady, ytVideoId, updateActiveLine]);

  // ── Native video handlers ────────────────────────────────────────────────────
  const handleTimeUpdate = () => {
    if (!videoRef.current) return;
    const ms = videoRef.current.currentTime * 1000;
    setCurrentMs(ms);
    updateActiveLine(ms);
  };

  const formatTime = (ms: number) => {
    const s = Math.floor(ms / 1000);
    return `${Math.floor(s / 60)}:${String(s % 60).padStart(2, '0')}`;
  };

  const seekToLine = (line: TranscriptLine) => {
    const seconds = line.start / 1000;
    if (ytVideoId && ytPlayerRef.current?.seekTo) {
      ytPlayerRef.current.seekTo(seconds, true);
      ytPlayerRef.current.playVideo();
    } else if (videoRef.current) {
      videoRef.current.currentTime = seconds;
      videoRef.current.play();
      setIsPlaying(true);
    }
  };

  const togglePlay = () => {
    if (ytVideoId && ytPlayerRef.current) {
      if (isPlaying) ytPlayerRef.current.pauseVideo(); else ytPlayerRef.current.playVideo();
    } else if (videoRef.current) {
      if (isPlaying) videoRef.current.pause(); else videoRef.current.play();
      setIsPlaying(!isPlaying);
    }
  };

  const toggleMute = () => {
    if (ytVideoId && ytPlayerRef.current) {
      if (isMuted) ytPlayerRef.current.unMute(); else ytPlayerRef.current.mute();
    } else if (videoRef.current) {
      videoRef.current.muted = !isMuted;
    }
    setIsMuted(!isMuted);
  };

  const handleComplete = async () => {
    try {
      await apiService.student.completeLesson(lessonId!, 100);
      useAppStore.getState().invalidateProgress();
    } catch { /* ignore */ }
    toast.success('Hoàn thành bài học!');
    navigate(-1);
  };

  // ── Thoại theo: thu âm từng câu và gọi API chấm phát âm ────────────────────
  const startRecordingForSentence = useCallback(async (sentenceIndex: number) => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      streamRef.current = stream;
      chunksRef.current = [];
      const mr = new MediaRecorder(stream);
      mediaRecorderRef.current = mr;
      mr.ondataavailable = (e) => {
        if (e.data.size > 0) chunksRef.current.push(e.data);
      };
      mr.onstop = async () => {
        stream.getTracks().forEach((t) => t.stop());
        if (recordingTimerRef.current) clearInterval(recordingTimerRef.current);
        setRecordingTime(0);
        setRecordingForSentenceIndex(null);
        setIsRecording(false);
        const blob = chunksRef.current.length
          ? new Blob(chunksRef.current, { type: mr.mimeType || 'audio/wav' })
          : null;
        if (!lessonId || blob == null) {
          toast.error('Không có bản ghi âm. Thử lại.');
          return;
        }
        try {
          const res = await apiService.pronunciation.getScore(Number(lessonId), sentenceIndex, blob);
          const data = res.data?.data;
          if (data) {
            setSentenceScores((prev) => ({ ...prev, [sentenceIndex]: data }));
            const overall = overallFromResponse(data);
            toast.success(`Câu ${sentenceIndex + 1}: ${overall} điểm`);
          } else {
            toast.error('Không nhận được kết quả chấm điểm');
          }
        } catch (err: any) {
          toast.error(err?.response?.data?.message || 'Chấm phát âm thất bại. Thử lại.');
        }
      };
      mr.start(200);
      setRecordingForSentenceIndex(sentenceIndex);
      setIsRecording(true);
      setRecordingTime(0);
      recordingTimerRef.current = setInterval(() => {
        setRecordingTime((t) => Math.min(t + 1, 60));
      }, 1000);
    } catch {
      toast.error('Không thể truy cập micro. Kiểm tra quyền trình duyệt.');
    }
  }, [lessonId]);

  const stopRecordingForSentence = useCallback(() => {
    if (mediaRecorderRef.current && mediaRecorderRef.current.state !== 'inactive') {
      mediaRecorderRef.current.stop();
    }
  }, []);

  if (loading) return (
    <div className="flex min-h-[60vh] items-center justify-center" style={{ backgroundColor: 'var(--page-bg)' }}>
      <Loader2 className="h-10 w-10 animate-spin text-primary" />
    </div>
  );

  const currentLine = activeLineIdx >= 0 ? transcript[activeLineIdx] : null;

  return (
    <div className="min-h-screen w-full" style={{ backgroundColor: 'var(--page-bg)' }}>
      <div className="max-w-6xl mx-auto p-4 lg:p-6 space-y-6">

        {/* ── Header ── */}
        <div className="flex flex-wrap items-center justify-between gap-3">
          <Button variant="ghost" onClick={() => navigate(-1)} className="gap-2 -ml-2 text-muted-foreground hover:text-foreground">
            <ArrowLeft className="w-4 h-4" /> Quay lại
          </Button>
          <div className="flex items-center gap-2">
            <Badge variant="secondary" className="font-medium">VIDEO</Badge>
            {lesson?.durationMinutes && <Badge variant="outline">{lesson.durationMinutes} phút</Badge>}
          </div>
        </div>

        <div>
          <h1 className="text-xl font-bold text-foreground">{lesson?.title ?? 'Bài học Video'}</h1>
          {lesson?.description && <p className="text-sm text-muted-foreground mt-0.5">{lesson.description}</p>}
        </div>

        {/* ── Display Toggles ── */}
        {transcript.length > 0 && (
          <div className="flex flex-wrap items-center gap-2 p-4 rounded-2xl border bg-card shadow-sm">
            <span className="text-xs font-semibold text-muted-foreground mr-1">Hiển thị:</span>
            <TogglePill active={showOverlaySub} onClick={() => setShowOverlaySub(v => !v)} label="📺 Subtitle video" activeColor="bg-primary text-primary-foreground" />
            <TogglePill active={showScript} onClick={() => setShowScript(v => !v)} label="📄 Script" activeColor="bg-blue-100 text-blue-700 dark:bg-blue-900 dark:text-blue-300" />
            <TogglePill active={showVI} onClick={() => setShowVI(v => !v)} label="🇻🇳 Dịch tiếng Việt" activeColor="bg-yellow-100 text-yellow-700 dark:bg-yellow-900 dark:text-yellow-300" />
            <TogglePill active={showIPA} onClick={() => setShowIPA(v => !v)} label="/IPA/ câu" activeColor="bg-emerald-100 text-emerald-700 dark:bg-emerald-900 dark:text-emerald-300" />
            <TogglePill active={showWordIPA} onClick={() => setShowWordIPA(v => !v)} label="🔤 IPA từng từ" activeColor="bg-purple-100 text-purple-700 dark:bg-purple-900 dark:text-purple-300" />
          </div>
        )}

        {/* ── Chế độ thoại theo: thanh chọn Ẩn/Hiện video ── */}
        {mode === 'shadowing' && transcript.length > 0 && (
          <div className="flex flex-wrap items-center gap-3 p-4 rounded-2xl border border-primary/20 bg-primary/5">
            <Mic className="w-5 h-5 text-primary flex-shrink-0" />
            <span className="font-medium text-foreground">Chế độ thoại theo</span>
            <Button
              variant="outline"
              size="sm"
              onClick={() => setShowVideoInShadowing((v) => !v)}
              className="gap-2"
            >
              {showVideoInShadowing ? <VideoOff className="w-4 h-4" /> : <Video className="w-4 h-4" />}
              {showVideoInShadowing ? 'Ẩn video' : 'Hiện video'}
            </Button>
            <span className="text-xs text-muted-foreground ml-auto">
              {Object.keys(sentenceScores).length}/{transcript.length} câu đã chấm
            </span>
          </div>
        )}

        {/* ── Main Grid ── */}
        <div className={`grid gap-4 ${showScript && transcript.length > 0 ? 'lg:grid-cols-5' : ''} ${mode === 'shadowing' && !showVideoInShadowing ? 'lg:grid-cols-1' : ''}`}>

          {/* Video — ẩn trong shadowing nếu user chọn ẩn video */}
          {(mode !== 'shadowing' || showVideoInShadowing) && (
          <div className={showScript && transcript.length > 0 ? 'lg:col-span-3' : ''}>
            <Card className="overflow-hidden rounded-2xl border shadow-lg bg-card">
              <div className="bg-black relative" style={{ aspectRatio: '16/9' }}>

                {ytVideoId ? (
                  <div id="yt-player" className="w-full h-full" />
                ) : lesson?.videoUrl ? (
                  <video
                    ref={videoRef}
                    className="w-full h-full object-contain"
                    onTimeUpdate={handleTimeUpdate}
                    onLoadedMetadata={e => setDuration(e.currentTarget.duration * 1000)}
                    onPlay={() => setIsPlaying(true)}
                    onPause={() => setIsPlaying(false)}
                    onEnded={() => setIsPlaying(false)}
                  >
                    <source src={lesson.videoUrl} type="video/mp4" />
                  </video>
                ) : (
                  <div className="w-full h-full flex flex-col items-center justify-center text-white/40 gap-3">
                    <Play className="w-16 h-16 opacity-20" />
                    <p className="text-sm text-center px-8">Chưa có video. Admin cần thêm URL YouTube hoặc link video.</p>
                  </div>
                )}

                {/* Subtitle overlay */}
                {showOverlaySub && currentLine && (
                  <div className="absolute bottom-14 left-0 right-0 px-6 pointer-events-none">
                    <div className="bg-black/85 backdrop-blur-sm rounded-xl p-3 text-center mx-auto max-w-2xl">
                      {/* English */}
                      {showWordIPA && currentLine.ipa ? (
                        <div className="flex flex-wrap justify-center gap-x-2 gap-y-0.5">
                          {buildWordIpaPairs(currentLine.text, currentLine.ipa).map((wp, i) => (
                            <span key={i} className="inline-flex flex-col items-center">
                              <span className="text-white text-base font-medium leading-tight">{wp.word}</span>
                              {wp.ipa && <span className="text-green-300 text-xs leading-tight">{wp.ipa}</span>}
                            </span>
                          ))}
                        </div>
                      ) : (
                        <p className="text-white text-lg font-medium">{currentLine.text}</p>
                      )}
                      {/* IPA câu */}
                      {showIPA && currentLine.ipa && !showWordIPA && (
                        <p className="text-green-300 text-sm font-mono mt-1">{currentLine.ipa}</p>
                      )}
                      {/* Dịch */}
                      {showVI && currentLine.viText && (
                        <p className="text-yellow-300 text-sm mt-1">{currentLine.viText}</p>
                      )}
                    </div>
                  </div>
                )}

                {/* Native video controls */}
                {!ytVideoId && (
                  <div className="absolute bottom-0 left-0 right-0 bg-gradient-to-t from-black/80 to-transparent p-3">
                    {/* seekbar */}
                    <div
                      className="w-full h-1.5 bg-white/20 rounded-full mb-3 cursor-pointer group"
                      onClick={e => {
                        if (!videoRef.current || !duration) return;
                        const r = e.currentTarget.getBoundingClientRect();
                        videoRef.current.currentTime = ((e.clientX - r.left) / r.width) * (duration / 1000);
                      }}
                    >
                      <div className="h-full bg-white rounded-full transition-all" style={{ width: `${duration ? (currentMs / duration) * 100 : 0}%` }} />
                    </div>
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-2">
                        <Button size="icon" variant="ghost" onClick={togglePlay} className="text-white hover:bg-white/20 h-8 w-8">
                          {isPlaying ? <Pause className="w-5 h-5" /> : <Play className="w-5 h-5" />}
                        </Button>
                        <Button size="icon" variant="ghost" onClick={() => { if (videoRef.current) { videoRef.current.currentTime = 0; videoRef.current.play(); setIsPlaying(true); } }} className="text-white hover:bg-white/20 h-7 w-7">
                          <RotateCcw className="w-4 h-4" />
                        </Button>
                        <Button size="icon" variant="ghost" onClick={toggleMute} className="text-white hover:bg-white/20 h-7 w-7">
                          {isMuted ? <VolumeX className="w-4 h-4" /> : <Volume2 className="w-4 h-4" />}
                        </Button>
                        <span className="text-white text-xs">{formatTime(currentMs)} / {formatTime(duration)}</span>
                      </div>
                    </div>
                  </div>
                )}
              </div>

              {/* YouTube toolbar */}
              {ytVideoId && (
                <div className="bg-muted/80 px-4 py-2 flex items-center gap-2 text-xs border-t">
                  <span className="text-muted-foreground">Controls:</span>
                  <Button size="icon" variant="ghost" onClick={togglePlay} className="h-6 w-6">
                    {isPlaying ? <Pause className="w-3 h-3" /> : <Play className="w-3 h-3" />}
                  </Button>
                  <Button size="icon" variant="ghost" onClick={toggleMute} className="h-6 w-6">
                    {isMuted ? <VolumeX className="w-3 h-3" /> : <Volume2 className="w-3 h-3" />}
                  </Button>
                  <span className="text-muted-foreground ml-auto tabular-nums">{formatTime(currentMs)}</span>
                </div>
              )}
            </Card>
          </div>
          )}

          {/* ── Script Panel ── */}
          {showScript && transcript.length > 0 && (
            <div className="lg:col-span-2">
              <Card className="flex flex-col h-full rounded-2xl border bg-card shadow-sm overflow-hidden">
                <div className="p-3 border-b bg-muted/30 flex items-center gap-2 flex-shrink-0">
                  <AlignLeft className="w-4 h-4 text-primary" />
                  <h3 className="font-semibold text-sm">Script</h3>
                  <span className="text-xs text-muted-foreground ml-auto">{transcript.length} câu</span>
                </div>

                <div ref={scriptRef} className="flex-1 overflow-y-auto divide-y divide-border" style={{ maxHeight: '480px' }}>
                  {transcript.map((line, idx) => {
                    const isActive = idx === activeLineIdx;
                    const pairs = (showWordIPA && line.ipa)
                      ? buildWordIpaPairs(line.text, line.ipa)
                      : null;
                    const score = sentenceScores[idx];
                    const isRecordingThis = recordingForSentenceIndex === idx;

                    return (
                      <div
                        key={idx}
                        ref={isActive ? activeLineRef : undefined}
                        onClick={mode !== 'shadowing' ? () => seekToLine(line) : undefined}
                        className={`px-3 py-2.5 group transition-colors ${
                          mode === 'shadowing' ? '' : 'cursor-pointer'
                        } ${
                          isActive && mode !== 'shadowing'
                            ? 'bg-primary/10 border-l-2 border-l-primary'
                            : mode === 'shadowing' && score
                              ? getScoreBg(overallFromResponse(score))
                              : 'hover:bg-muted/50'
                        }`}
                      >
                        <div className="flex items-start gap-2">
                          {/* Timestamp */}
                          <span className="text-xs text-muted-foreground mt-0.5 flex-shrink-0 w-9 tabular-nums">
                            {formatTime(line.start)}
                          </span>

                          <div className="flex-1 min-w-0 space-y-0.5">
                            {/* English — word IPA or plain */}
                            {pairs ? (
                              <div className="flex flex-wrap gap-x-1.5 gap-y-0.5">
                                {pairs.map((wp, i) => (
                                  <span key={i} className="inline-flex flex-col items-center">
                                    <span className={`text-sm leading-tight ${isActive && mode !== 'shadowing' ? 'font-semibold text-primary' : 'text-foreground'}`}>
                                      {wp.word}
                                    </span>
                                    {wp.ipa && (
                                      <span className="text-xs text-purple-500 dark:text-purple-400 leading-tight font-mono">
                                        {wp.ipa}
                                      </span>
                                    )}
                                  </span>
                                ))}
                              </div>
                            ) : (
                              <p className={`text-sm leading-snug ${isActive && mode !== 'shadowing' ? 'font-semibold text-primary' : 'text-foreground'}`}>
                                {line.text}
                              </p>
                            )}

                            {/* IPA câu (khi không dùng word IPA) */}
                            {showIPA && !showWordIPA && line.ipa && (
                              <p className="text-xs text-green-600 dark:text-green-400 font-mono leading-tight">
                                /{line.ipa}/
                              </p>
                            )}

                            {/* Dịch tiếng Việt */}
                            {showVI && line.viText && (
                              <p className="text-xs text-yellow-600 dark:text-yellow-400 leading-tight italic">
                                {line.viText}
                              </p>
                            )}

                            {/* Thoại theo: nút Thu âm + điểm phát âm cụ thể */}
                            {mode === 'shadowing' && (
                              <div className="mt-2 space-y-2">
                                {isRecordingThis ? (
                                  <div className="flex flex-col items-center py-2">
                                    <RecordingButton
                                      isRecording={isRecording}
                                      recordingTime={recordingTime}
                                      maxDuration={60}
                                      onStartRecording={() => {}}
                                      onStopRecording={stopRecordingForSentence}
                                      size="sm"
                                      showWaveform={false}
                                    />
                                    <p className="text-xs text-gray-500 mt-1">Nhấn nút để dừng thu âm</p>
                                  </div>
                                ) : (
                                  <Button
                                    type="button"
                                    size="sm"
                                    variant="outline"
                                    className="gap-1.5"
                                    onClick={(e) => { e.stopPropagation(); startRecordingForSentence(idx); }}
                                  >
                                    <Mic className="w-3.5 h-3.5" /> Thu âm
                                  </Button>
                                )}
                                {score && (
                                  <div className={`rounded-lg p-2 text-sm ${getScoreBg(overallFromResponse(score))}`}>
                                    <div className="flex items-center gap-2 flex-wrap">
                                      <span className={`font-bold ${getScoreColor(overallFromResponse(score))}`}>
                                        {overallFromResponse(score)} điểm
                                      </span>
                                      <span className="text-muted-foreground">·</span>
                                      <span>Phát âm {(score.pronunciationScore ?? 0).toFixed(0)}%</span>
                                      <span>·</span>
                                      <span>Độ chính xác {(score.accuracyScore ?? 0).toFixed(0)}%</span>
                                      <span>·</span>
                                      <span>Độ trôi chảy {(score.fluencyScore ?? 0).toFixed(0)}%</span>
                                    </div>
                                    {score.recognizedText && (
                                      <p className="text-xs text-muted-foreground mt-1 truncate" title={score.recognizedText}>
                                        Bạn nói: {score.recognizedText}
                                      </p>
                                    )}
                                  </div>
                                )}
                              </div>
                            )}
                          </div>

                          {mode !== 'shadowing' && (
                          <ChevronRight className={`w-3 h-3 flex-shrink-0 mt-1 transition-opacity ${isActive ? 'opacity-100 text-primary' : 'opacity-0 group-hover:opacity-60 text-muted-foreground'}`} />
                          )}
                        </div>
                      </div>
                    );
                  })}
                </div>
              </Card>
            </div>
          )}
        </div>

        {/* ── CTA: Sau khi xem, chọn Thoại theo hoặc Bỏ qua ── */}
        {mode === 'watch' && transcript.length > 0 && (
          <Card className="p-6 rounded-2xl border border-primary/20 bg-primary/5">
            <p className="text-base text-foreground mb-4">
              Bạn đã xem qua nội dung. <strong>Thoại theo</strong> từng câu để được chấm phát âm ngay và cải thiện.
            </p>
            <div className="flex flex-wrap gap-3">
              <Button
                onClick={() => setMode('shadowing')}
                className="gap-2 bg-primary hover:bg-primary-hover text-primary-foreground shadow-md"
              >
                <Mic className="w-4 h-4" /> Bắt đầu thoại theo
              </Button>
              <Button variant="outline" onClick={handleComplete} className="gap-2">
                Bỏ qua, hoàn thành bài học
              </Button>
            </div>
          </Card>
        )}

        {/* Processing banner */}
        {transcript.length === 0 && lesson?.s3Status === 'PROCESSING' && (
          <Card className="p-4 rounded-2xl border border-amber-200 dark:border-amber-800 bg-amber-50 dark:bg-amber-900/20 flex items-center gap-3">
            <Loader2 className="w-5 h-5 animate-spin text-amber-600 flex-shrink-0" />
            <div>
              <p className="font-medium text-amber-800 dark:text-amber-300">Đang phân tích video...</p>
              <p className="text-xs text-amber-600 dark:text-amber-400 mt-0.5">
                Đang tạo subtitle, dịch tiếng Việt và IPA. Trang tự cập nhật khi xong.
              </p>
            </div>
          </Card>
        )}

        {/* Complete */}
        <div className="flex justify-end pt-2">
          <Button onClick={handleComplete} className="bg-green-600 hover:bg-green-700 text-white gap-2 px-6 rounded-xl shadow-md">
            <CheckCircle2 className="w-5 h-5" /> Hoàn thành bài học
          </Button>
        </div>
      </div>
    </div>
  );
}
