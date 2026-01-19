import { useState, useRef, useEffect } from 'react';
import { useAppStore } from '@/store/appStore';
import { Card } from '@/app/components/ui/card';
import { Button } from '@/app/components/ui/button';
import { Badge } from '@/app/components/ui/badge';
import { Avatar } from '@/app/components/ui/avatar';
import { 
  Mic, 
  MicOff,
  ChevronLeft,
  Send,
  Bot,
  User,
  CheckCircle,
  Volume2
} from 'lucide-react';
import { motion, AnimatePresence } from 'motion/react';
import { getLessonById } from '@/services/mockData';

interface Message {
  id: string;
  role: 'ai' | 'user';
  text: string;
  audioUrl?: string;
  timestamp: Date;
}

const aiPrompts = [
  "Hello! Nice to meet you. What's your name?",
  "That's a lovely name! Where are you from?",
  "Interesting! What do you do for a living?",
  "That sounds exciting! What do you like to do in your free time?",
  "Great conversation! Thanks for chatting with me!",
];

export function AIConversation() {
  const { selectedLessonId, setCurrentView, completeLesson, addXP } = useAppStore();
  const lesson = getLessonById(selectedLessonId || '');
  
  const [messages, setMessages] = useState<Message[]>([
    {
      id: '1',
      role: 'ai',
      text: aiPrompts[0],
      timestamp: new Date(),
    }
  ]);
  const [currentPromptIndex, setCurrentPromptIndex] = useState(0);
  const [isRecording, setIsRecording] = useState(false);
  const [recordingTime, setRecordingTime] = useState(0);
  const [inputText, setInputText] = useState('');
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const recordingTimerRef = useRef<NodeJS.Timeout | null>(null);

  // Auto scroll to bottom
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  // Recording timer
  useEffect(() => {
    if (isRecording) {
      recordingTimerRef.current = setInterval(() => {
        setRecordingTime((prev) => {
          if (prev >= 30) {
            handleStopRecording();
            return 30;
          }
          return prev + 0.1;
        });
      }, 100);
    } else {
      if (recordingTimerRef.current) {
        clearInterval(recordingTimerRef.current);
      }
    }
    return () => {
      if (recordingTimerRef.current) {
        clearInterval(recordingTimerRef.current);
      }
    };
  }, [isRecording]);

  const handleStartRecording = () => {
    setIsRecording(true);
    setRecordingTime(0);
  };

  const handleStopRecording = () => {
    setIsRecording(false);
    
    // Simulate transcription
    setTimeout(() => {
      handleSendMessage("Yes, I understand. Thank you!");
    }, 800);
  };

  const handleSendMessage = (text?: string) => {
    const messageText = text || inputText.trim();
    if (!messageText) return;

    // Add user message
    const userMessage: Message = {
      id: `user-${Date.now()}`,
      role: 'user',
      text: messageText,
      timestamp: new Date(),
    };

    setMessages(prev => [...prev, userMessage]);
    setInputText('');
    setRecordingTime(0);

    // Simulate AI response after delay
    setTimeout(() => {
      if (currentPromptIndex < aiPrompts.length - 1) {
        const nextIndex = currentPromptIndex + 1;
        const aiMessage: Message = {
          id: `ai-${Date.now()}`,
          role: 'ai',
          text: aiPrompts[nextIndex],
          timestamp: new Date(),
        };
        setMessages(prev => [...prev, aiMessage]);
        setCurrentPromptIndex(nextIndex);
      } else {
        // Conversation complete
        setTimeout(() => {
          handleComplete();
        }, 2000);
      }
    }, 1500);
  };

  const handleComplete = () => {
    completeLesson(selectedLessonId || '', 90);
    addXP(100);
    setCurrentView('session-detail');
  };

  const handlePlayAudio = (messageId: string) => {
    console.log('Playing audio for message:', messageId);
  };

  if (!lesson) {
    return (
      <div className="container mx-auto p-4">
        <p>Không tìm thấy bài học</p>
      </div>
    );
  }

  const isConversationComplete = currentPromptIndex >= aiPrompts.length - 1 && 
                                  messages[messages.length - 1]?.role === 'ai';

  return (
    <div className="min-h-screen bg-gradient-to-br from-purple-50 via-pink-50 to-blue-50 dark:from-gray-900 dark:via-gray-900 dark:to-gray-900">
      <div className="container mx-auto px-4 py-6 max-w-4xl">
        {/* Header */}
        <div className="mb-6 flex items-center justify-between">
          <Button
            variant="ghost"
            onClick={() => setCurrentView('session-detail')}
            className="gap-2"
          >
            <ChevronLeft className="h-4 w-4" />
            Quay lại
          </Button>
          <Badge variant="outline" className="gap-1">
            <Bot className="h-3 w-3" />
            AI Conversation
          </Badge>
        </div>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5 }}
        >
          {/* Title */}
          <div className="mb-6">
            <h1 className="mb-2 text-3xl font-bold">{lesson.title}</h1>
            <p className="text-gray-600 dark:text-gray-400">{lesson.description}</p>
          </div>

          {/* Chat Container */}
          <Card className="mb-4 overflow-hidden">
            {/* Messages */}
            <div className="h-[500px] overflow-y-auto p-6 space-y-4 bg-gradient-to-b from-white to-gray-50 dark:from-gray-800 dark:to-gray-900">
              <AnimatePresence>
                {messages.map((message, index) => (
                  <motion.div
                    key={message.id}
                    initial={{ opacity: 0, y: 20, scale: 0.95 }}
                    animate={{ opacity: 1, y: 0, scale: 1 }}
                    transition={{ delay: index * 0.1 }}
                    className={`flex gap-3 ${message.role === 'user' ? 'flex-row-reverse' : ''}`}
                  >
                    {/* Avatar */}
                    <Avatar className={`flex-shrink-0 h-10 w-10 ${
                      message.role === 'ai' 
                        ? 'bg-gradient-to-br from-blue-500 to-purple-600' 
                        : 'bg-gradient-to-br from-green-500 to-emerald-600'
                    }`}>
                      <div className="flex h-full w-full items-center justify-center text-white">
                        {message.role === 'ai' ? (
                          <Bot className="h-5 w-5" />
                        ) : (
                          <User className="h-5 w-5" />
                        )}
                      </div>
                    </Avatar>

                    {/* Message Bubble */}
                    <div className={`flex-1 max-w-[70%] ${message.role === 'user' ? 'items-end' : ''}`}>
                      <div
                        className={`rounded-2xl p-4 ${
                          message.role === 'ai'
                            ? 'bg-white dark:bg-gray-800 shadow-md'
                            : 'bg-gradient-to-br from-green-500 to-emerald-600 text-white shadow-lg'
                        }`}
                      >
                        <p className="text-sm md:text-base">{message.text}</p>
                        
                        {message.role === 'ai' && (
                          <Button
                            size="sm"
                            variant="ghost"
                            onClick={() => handlePlayAudio(message.id)}
                            className="mt-2 h-8 gap-1 text-xs"
                          >
                            <Volume2 className="h-3 w-3" />
                            Nghe
                          </Button>
                        )}
                      </div>
                      <p className="mt-1 text-xs text-gray-500 dark:text-gray-400 px-2">
                        {message.timestamp.toLocaleTimeString('vi-VN', { 
                          hour: '2-digit', 
                          minute: '2-digit' 
                        })}
                      </p>
                    </div>
                  </motion.div>
                ))}
              </AnimatePresence>

              {/* Typing Indicator */}
              {messages[messages.length - 1]?.role === 'user' && !isConversationComplete && (
                <motion.div
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  className="flex gap-3"
                >
                  <Avatar className="flex-shrink-0 h-10 w-10 bg-gradient-to-br from-blue-500 to-purple-600">
                    <div className="flex h-full w-full items-center justify-center text-white">
                      <Bot className="h-5 w-5" />
                    </div>
                  </Avatar>
                  <div className="rounded-2xl bg-white dark:bg-gray-800 shadow-md p-4">
                    <div className="flex gap-1">
                      <motion.div
                        animate={{ y: [0, -8, 0] }}
                        transition={{ repeat: Infinity, duration: 0.6, delay: 0 }}
                        className="h-2 w-2 rounded-full bg-gray-400"
                      />
                      <motion.div
                        animate={{ y: [0, -8, 0] }}
                        transition={{ repeat: Infinity, duration: 0.6, delay: 0.2 }}
                        className="h-2 w-2 rounded-full bg-gray-400"
                      />
                      <motion.div
                        animate={{ y: [0, -8, 0] }}
                        transition={{ repeat: Infinity, duration: 0.6, delay: 0.4 }}
                        className="h-2 w-2 rounded-full bg-gray-400"
                      />
                    </div>
                  </div>
                </motion.div>
              )}

              <div ref={messagesEndRef} />
            </div>

            {/* Input Area */}
            {!isConversationComplete && (
              <div className="border-t p-4 bg-white dark:bg-gray-800">
                <div className="flex items-center gap-2">
                  {/* Voice Input */}
                  <motion.button
                    whileHover={{ scale: 1.05 }}
                    whileTap={{ scale: 0.95 }}
                    onClick={isRecording ? handleStopRecording : handleStartRecording}
                    className={`flex h-12 w-12 flex-shrink-0 items-center justify-center rounded-full transition-all ${
                      isRecording
                        ? 'bg-red-500 shadow-lg shadow-red-500/50 animate-pulse'
                        : 'bg-gradient-to-br from-green-500 to-emerald-600 hover:shadow-lg'
                    }`}
                  >
                    {isRecording ? (
                      <MicOff className="h-5 w-5 text-white" />
                    ) : (
                      <Mic className="h-5 w-5 text-white" />
                    )}
                  </motion.button>

                  {/* Recording Timer */}
                  {isRecording && (
                    <motion.div
                      initial={{ opacity: 0, scale: 0.8 }}
                      animate={{ opacity: 1, scale: 1 }}
                      className="flex-1 text-center"
                    >
                      <p className="text-sm font-medium text-red-600 dark:text-red-400">
                        Đang ghi âm... {recordingTime.toFixed(1)}s
                      </p>
                    </motion.div>
                  )}

                  {/* Text Input */}
                  {!isRecording && (
                    <>
                      <input
                        type="text"
                        value={inputText}
                        onChange={(e) => setInputText(e.target.value)}
                        onKeyPress={(e) => e.key === 'Enter' && handleSendMessage()}
                        placeholder="Nhập câu trả lời..."
                        className="flex-1 rounded-lg border px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600"
                      />
                      <Button
                        onClick={() => handleSendMessage()}
                        disabled={!inputText.trim()}
                        className="h-12 w-12 p-0 bg-gradient-to-br from-blue-500 to-purple-600 hover:from-blue-600 hover:to-purple-700"
                      >
                        <Send className="h-5 w-5" />
                      </Button>
                    </>
                  )}
                </div>

                <p className="mt-2 text-xs text-gray-500 dark:text-gray-400 text-center">
                  Nhấn vào micro để nói hoặc nhập văn bản
                </p>
              </div>
            )}

            {/* Complete Message */}
            {isConversationComplete && (
              <motion.div
                initial={{ opacity: 0, scale: 0.9 }}
                animate={{ opacity: 1, scale: 1 }}
                className="border-t p-6 bg-gradient-to-br from-green-50 to-emerald-50 dark:from-green-900/20 dark:to-emerald-900/20 text-center"
              >
                <CheckCircle className="h-12 w-12 mx-auto mb-3 text-green-600 dark:text-green-400" />
                <h3 className="text-xl font-bold mb-2">Xuất sắc! 🎉</h3>
                <p className="text-gray-600 dark:text-gray-400 mb-4">
                  Bạn đã hoàn thành cuộc hội thoại!
                </p>
                <Button
                  size="lg"
                  onClick={handleComplete}
                  className="gap-2 bg-gradient-to-r from-green-500 to-emerald-500 hover:from-green-600 hover:to-emerald-600"
                >
                  <CheckCircle className="h-5 w-5" />
                  Hoàn thành bài học (+100 XP)
                </Button>
              </motion.div>
            )}
          </Card>

          {/* Tips */}
          <Card className="p-4 bg-gradient-to-br from-blue-50 to-purple-50 dark:from-blue-900/20 dark:to-purple-900/20 border-blue-200 dark:border-blue-800">
            <h3 className="mb-2 font-bold flex items-center gap-2">
              💬 Mẹo hội thoại
            </h3>
            <ul className="space-y-1 text-sm text-gray-700 dark:text-gray-300">
              <li>• Trả lời bằng giọng nói hoặc gõ văn bản</li>
              <li>• Cố gắng trả lời đầy đủ và tự nhiên</li>
              <li>• Nhấn nút nghe để nghe lại câu hỏi của AI</li>
              <li>• Đừng lo lắng nếu mắc lỗi - hãy thử lại!</li>
            </ul>
          </Card>
        </motion.div>
      </div>
    </div>
  );
}
