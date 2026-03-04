import { useState, useRef, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router';
import { Button } from '@/app/components/ui/button';
import { Card } from '@/app/components/ui/card';
import { Badge } from '@/app/components/ui/badge';
import { Mic, Send, Volume2, ArrowLeft, CheckCircle2, Bot, User } from 'lucide-react';
import { toast } from 'sonner';
import { Avatar, AvatarFallback, AvatarImage } from '@/app/components/ui/avatar';
import { useCompleteLesson } from '@/hooks/useCompleteLesson';

interface Message {
  id: number;
  sender: 'ai' | 'user';
  text: string;
  timestamp: Date;
  audioUrl?: string;
}

export function AIConversationLesson() {
  const navigate = useNavigate();
  const { lessonId } = useParams();
  const { completeAndSave } = useCompleteLesson();
  const messagesEndRef = useRef<HTMLDivElement>(null);
  
  const [messages, setMessages] = useState<Message[]>([]);
  const [isRecording, setIsRecording] = useState(false);
  const [isAISpeaking, setIsAISpeaking] = useState(false);
  const [turnCount, setTurnCount] = useState(0);

  const scenario = {
    id: lessonId,
    title: 'Booking a Hotel Room',
    description: 'Practice a conversation about making a hotel reservation',
    targetTurns: 10,
    xp: 75,
  };

  useEffect(() => {
    // Initial AI greeting
    const greeting: Message = {
      id: 1,
      sender: 'ai',
      text: "Hello! Welcome to Grand Hotel. How may I help you today?",
      timestamp: new Date(),
    };
    setMessages([greeting]);
    speakText(greeting.text);
  }, []);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const speakText = (text: string) => {
    setIsAISpeaking(true);
    const utterance = new SpeechSynthesisUtterance(text);
    utterance.lang = 'en-US';
    utterance.rate = 0.9;
    utterance.onend = () => setIsAISpeaking(false);
    window.speechSynthesis.speak(utterance);
  };

  const getAIResponse = (userMessage: string): string => {
    const lowerMessage = userMessage.toLowerCase();
    
    if (lowerMessage.includes('book') || lowerMessage.includes('reservation')) {
      return "Of course! I'd be happy to help you with a reservation. For which dates are you looking to stay?";
    } else if (lowerMessage.includes('date') || /\d/.test(userMessage)) {
      return "Great! And how many guests will be staying?";
    } else if (lowerMessage.includes('guest') || lowerMessage.includes('people')) {
      return "Perfect! Would you prefer a single room, double room, or a suite?";
    } else if (lowerMessage.includes('room') || lowerMessage.includes('single') || lowerMessage.includes('double')) {
      return "Excellent choice! Would you like breakfast included with your stay?";
    } else if (lowerMessage.includes('yes') || lowerMessage.includes('breakfast')) {
      return "Wonderful! May I have your name for the reservation, please?";
    } else if (lowerMessage.includes('name') || lowerMessage.includes('my name')) {
      return "Thank you! And may I have a contact number where we can reach you?";
    } else if (/\d{3}/.test(userMessage)) {
      return "Perfect! I have your reservation confirmed. You'll receive a confirmation email shortly. Is there anything else I can help you with?";
    } else if (lowerMessage.includes('no') || lowerMessage.includes('that')) {
      return "Thank you for choosing Grand Hotel! We look forward to your stay. Have a great day!";
    }
    
    return "I see. Could you please tell me more about what you're looking for?";
  };

  const startRecording = () => {
    setIsRecording(true);
    
    // Simulate recording for 2 seconds
    setTimeout(() => {
      setIsRecording(false);
      
      // Mock speech recognition
      const responses = [
        "I'd like to book a room for next weekend.",
        "For two guests, please.",
        "A double room would be perfect.",
        "Yes, breakfast included would be great.",
        "My name is John Smith.",
        "You can reach me at 555-1234.",
        "No, that's everything. Thank you!",
      ];
      
      const userText = responses[Math.min(turnCount, responses.length - 1)];
      handleSendMessage(userText);
    }, 2000);
  };

  const handleSendMessage = (text: string) => {
    const userMessage: Message = {
      id: messages.length + 1,
      sender: 'user',
      text,
      timestamp: new Date(),
    };

    setMessages([...messages, userMessage]);
    setTurnCount(turnCount + 1);

    // Get AI response
    setTimeout(() => {
      const aiResponseText = getAIResponse(text);
      const aiMessage: Message = {
        id: messages.length + 2,
        sender: 'ai',
        text: aiResponseText,
        timestamp: new Date(),
      };
      
      setMessages(prev => [...prev, aiMessage]);
      speakText(aiResponseText);
      setTurnCount(prev => prev + 1);
    }, 1000);
  };

  const handleComplete = async () => {
    if (!lessonId) return;
    const ok = await completeAndSave(lessonId, 85);
    if (ok) {
      toast.success(`🎉 Conversation Completed! +${scenario.xp} XP`);
      navigate('/student/learning');
    }
  };

  const progress = (turnCount / scenario.targetTurns) * 100;

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-50 dark:from-gray-900 dark:to-gray-800 p-6">
      <div className="max-w-4xl mx-auto space-y-6">
        {/* Header */}
        <div className="flex items-center justify-between">
          <Button
            variant="ghost"
            onClick={() => navigate('/student/learning')}
            className="gap-2"
          >
            <ArrowLeft className="w-4 h-4" />
            Back
          </Button>
          <div className="flex items-center gap-4">
            <div className="text-right">
              <p className="text-sm text-gray-600 dark:text-gray-400">
                Progress: {turnCount}/{scenario.targetTurns} turns
              </p>
              <div className="w-32 h-2 bg-gray-200 dark:bg-gray-700 rounded-full overflow-hidden">
                <div
                  className="h-full bg-green-600 transition-all duration-300"
                  style={{ width: `${progress}%` }}
                />
              </div>
            </div>
          </div>
        </div>

        {/* Scenario Info */}
        <Card className="p-6">
          <div className="flex items-start gap-4">
            <div className="w-12 h-12 rounded-full bg-blue-100 dark:bg-blue-900 flex items-center justify-center">
              <Bot className="w-6 h-6 text-blue-600 dark:text-blue-400" />
            </div>
            <div className="flex-1">
              <h2 className="text-2xl font-bold mb-2">{scenario.title}</h2>
              <p className="text-gray-600 dark:text-gray-400">{scenario.description}</p>
            </div>
            <Badge variant="secondary" className="text-sm">
              {scenario.xp} XP
            </Badge>
          </div>
        </Card>

        {/* Chat Messages */}
        <Card className="p-6 h-[500px] flex flex-col">
          <div className="flex-1 overflow-y-auto space-y-4 mb-4">
            {messages.map((message) => (
              <div
                key={message.id}
                className={`flex gap-3 ${message.sender === 'user' ? 'flex-row-reverse' : ''}`}
              >
                <Avatar className="w-10 h-10">
                  {message.sender === 'ai' ? (
                    <>
                      <AvatarFallback className="bg-blue-100 dark:bg-blue-900">
                        <Bot className="w-5 h-5 text-blue-600 dark:text-blue-400" />
                      </AvatarFallback>
                    </>
                  ) : (
                    <>
                      <AvatarFallback className="bg-green-100 dark:bg-green-900">
                        <User className="w-5 h-5 text-green-600 dark:text-green-400" />
                      </AvatarFallback>
                    </>
                  )}
                </Avatar>
                
                <div className={`flex flex-col ${message.sender === 'user' ? 'items-end' : ''}`}>
                  <div
                    className={`max-w-md rounded-2xl px-4 py-3 ${
                      message.sender === 'ai'
                        ? 'bg-gray-100 dark:bg-gray-800 text-gray-900 dark:text-gray-100'
                        : 'bg-green-600 text-white'
                    }`}
                  >
                    <p>{message.text}</p>
                  </div>
                  {message.sender === 'ai' && (
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={() => speakText(message.text)}
                      className="mt-1 h-6 px-2 text-xs gap-1"
                    >
                      <Volume2 className="w-3 h-3" />
                      Listen
                    </Button>
                  )}
                </div>
              </div>
            ))}
            <div ref={messagesEndRef} />
          </div>

          {/* Input Area */}
          <div className="flex gap-2 pt-4 border-t">
            <Button
              onClick={startRecording}
              disabled={isRecording || isAISpeaking}
              className={`${
                isRecording
                  ? 'bg-red-600 hover:bg-red-700'
                  : 'bg-blue-600 hover:bg-blue-700'
              } gap-2`}
            >
              <Mic className="w-5 h-5" />
              {isRecording ? 'Recording...' : 'Speak'}
            </Button>
            
            {turnCount >= scenario.targetTurns && (
              <Button
                onClick={handleComplete}
                className="bg-green-600 hover:bg-green-700 text-white gap-2 ml-auto"
              >
                <CheckCircle2 className="w-5 h-5" />
                Complete Lesson (+{scenario.xp} XP)
              </Button>
            )}
          </div>
        </Card>

        {/* Tips */}
        <Card className="p-4 bg-blue-50 dark:bg-blue-900/20 border-blue-200 dark:border-blue-800">
          <p className="text-sm text-blue-800 dark:text-blue-300">
            💡 <strong>Tip:</strong> Take your time and speak clearly. The AI will wait for you to finish before responding.
          </p>
        </Card>
      </div>
    </div>
  );
}
