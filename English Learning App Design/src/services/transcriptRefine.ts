import apiService from './apiService';

/**
 * Post-process noisy ASR text (Whisper / realtime) using the same chat backend.
 * Does not pass lessonContext at top level — avoids the extra "conversation tutor" system prompt in AiChatService.
 */
export async function refineVoiceTranscript(raw: string, hint?: string): Promise<string> {
  const t = raw?.trim();
  if (!t) return '';
  const hintBlock = hint?.trim()
    ? `Lesson hints (titles / topic words only, for choosing spellings — do not invent content):\n${hint.trim()}\n\n`
    : '';

  try {
    const res = await apiService.ai.chat({
      messages: [
        {
          role: 'system',
          content:
            'You fix noisy automatic speech-to-text. '
            + 'Output ONLY the corrected transcript in the SAME language(s) as the input '
            + '(English stays English; Vietnamese stays Vietnamese). '
            + 'Fix spelling, homophones, missing apostrophes, and obvious misheard words. '
            + 'Do not add sentences, do not answer questions, do not explain. '
            + 'No quotes. If it is already correct, return it unchanged.',
        },
        {
          role: 'user',
          content: `${hintBlock}Transcript:\n${t}`,
        },
      ],
    });
    const rawResp = res.data as any;
    const content = rawResp?.data?.content ?? rawResp?.content ?? '';
    let out = typeof content === 'string' ? content.trim() : '';
    out = out.replace(/^["'`]+|["'`]+$/g, '').trim();
    if (!out || out.length < 1) return t;
    return out;
  } catch {
    return t;
  }
}
