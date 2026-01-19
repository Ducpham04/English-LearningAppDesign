import { useState } from 'react';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogFooter } from '@/app/components/ui/dialog';
import { Button } from '@/app/components/ui/button';
import { Input } from '@/app/components/ui/input';
import { Label } from '@/app/components/ui/label';
import { Textarea } from '@/app/components/ui/textarea';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/app/components/ui/select';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/app/components/ui/tabs';
import { Video, Mic, Bot, BookOpen, Plus, X, Upload } from 'lucide-react';
import { toast } from 'sonner';

interface CreateLessonModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSuccess?: () => void;
}

interface Question {
  id: string;
  question: string;
  options: string[];
  correct: number;
}

export function CreateLessonModal({ open, onOpenChange, onSuccess }: CreateLessonModalProps) {
  const [lessonType, setLessonType] = useState<'video' | 'pronunciation' | 'conversation' | 'flashcard'>('video');
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    level: '',
    duration: '10',
    xp: '50',
  });

  // Video lesson specific
  const [videoUrl, setVideoUrl] = useState('');
  const [subtitles, setSubtitles] = useState<Array<{ time: string; speaker: string; text: string }>>([]);
  const [questions, setQuestions] = useState<Question[]>([]);

  // Pronunciation lesson specific
  const [words, setWords] = useState<Array<{ word: string; phonetic: string; difficulty: string }>>([]);

  // AI Conversation specific
  const [scenario, setScenario] = useState('');
  const [targetTurns, setTargetTurns] = useState('10');

  // Flashcard specific
  const [flashcards, setFlashcards] = useState<Array<{ word: string; translation: string; example: string }>>([]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!formData.title || !formData.level) {
      toast.error('Please fill in all required fields');
      return;
    }

    toast.success(`${lessonType.charAt(0).toUpperCase() + lessonType.slice(1)} lesson created successfully! 🎉`);
    onOpenChange(false);
    onSuccess?.();
    resetForm();
  };

  const resetForm = () => {
    setFormData({
      title: '',
      description: '',
      level: '',
      duration: '10',
      xp: '50',
    });
    setVideoUrl('');
    setSubtitles([]);
    setQuestions([]);
    setWords([]);
    setScenario('');
    setFlashcards([]);
  };

  const addQuestion = () => {
    setQuestions([...questions, {
      id: Date.now().toString(),
      question: '',
      options: ['', '', '', ''],
      correct: 0,
    }]);
  };

  const addWord = () => {
    setWords([...words, { word: '', phonetic: '', difficulty: 'medium' }]);
  };

  const addFlashcard = () => {
    setFlashcards([...flashcards, { word: '', translation: '', example: '' }]);
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-4xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>Create New Lesson</DialogTitle>
          <DialogDescription>
            Create a new lesson with content and exercises for your students.
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-6">
          {/* Lesson Type Selection */}
          <Tabs value={lessonType} onValueChange={(v) => setLessonType(v as any)} className="w-full">
            <TabsList className="grid grid-cols-4 w-full">
              <TabsTrigger value="video" className="gap-2">
                <Video className="w-4 h-4" />
                Video
              </TabsTrigger>
              <TabsTrigger value="pronunciation" className="gap-2">
                <Mic className="w-4 h-4" />
                Pronunciation
              </TabsTrigger>
              <TabsTrigger value="conversation" className="gap-2">
                <Bot className="w-4 h-4" />
                AI Chat
              </TabsTrigger>
              <TabsTrigger value="flashcard" className="gap-2">
                <BookOpen className="w-4 h-4" />
                Flashcard
              </TabsTrigger>
            </TabsList>

            {/* Basic Info (Common for all types) */}
            <div className="mt-6 space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="title">Lesson Title *</Label>
                  <Input
                    id="title"
                    value={formData.title}
                    onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                    placeholder="e.g., Restaurant Conversation"
                    required
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="level">Level *</Label>
                  <Select value={formData.level} onValueChange={(value) => setFormData({ ...formData, level: value })}>
                    <SelectTrigger>
                      <SelectValue placeholder="Select level" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="beginner">Beginner</SelectItem>
                      <SelectItem value="intermediate">Intermediate</SelectItem>
                      <SelectItem value="advanced">Advanced</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="description">Description</Label>
                <Textarea
                  id="description"
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  placeholder="Brief description of the lesson..."
                  rows={2}
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="duration">Duration (minutes)</Label>
                  <Input
                    id="duration"
                    type="number"
                    value={formData.duration}
                    onChange={(e) => setFormData({ ...formData, duration: e.target.value })}
                    min="5"
                    max="60"
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="xp">XP Reward</Label>
                  <Input
                    id="xp"
                    type="number"
                    value={formData.xp}
                    onChange={(e) => setFormData({ ...formData, xp: e.target.value })}
                    min="10"
                    max="200"
                  />
                </div>
              </div>
            </div>

            {/* Video Lesson Content */}
            <TabsContent value="video" className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="videoUrl">Video URL or Upload</Label>
                <div className="flex gap-2">
                  <Input
                    id="videoUrl"
                    value={videoUrl}
                    onChange={(e) => setVideoUrl(e.target.value)}
                    placeholder="https://youtube.com/... or upload file"
                  />
                  <Button type="button" variant="outline">
                    <Upload className="w-4 h-4" />
                  </Button>
                </div>
              </div>

              <div className="space-y-2">
                <div className="flex items-center justify-between">
                  <Label>Comprehension Questions</Label>
                  <Button type="button" size="sm" onClick={addQuestion} variant="outline">
                    <Plus className="w-4 h-4 mr-1" />
                    Add Question
                  </Button>
                </div>
                {questions.map((q, idx) => (
                  <div key={q.id} className="border rounded-lg p-4 space-y-2">
                    <Input
                      placeholder={`Question ${idx + 1}`}
                      value={q.question}
                      onChange={(e) => {
                        const newQuestions = [...questions];
                        newQuestions[idx].question = e.target.value;
                        setQuestions(newQuestions);
                      }}
                    />
                    <div className="grid grid-cols-2 gap-2">
                      {q.options.map((opt, optIdx) => (
                        <Input
                          key={optIdx}
                          placeholder={`Option ${optIdx + 1}`}
                          value={opt}
                          onChange={(e) => {
                            const newQuestions = [...questions];
                            newQuestions[idx].options[optIdx] = e.target.value;
                            setQuestions(newQuestions);
                          }}
                        />
                      ))}
                    </div>
                    <Select
                      value={q.correct.toString()}
                      onValueChange={(v) => {
                        const newQuestions = [...questions];
                        newQuestions[idx].correct = parseInt(v);
                        setQuestions(newQuestions);
                      }}
                    >
                      <SelectTrigger>
                        <SelectValue placeholder="Correct answer" />
                      </SelectTrigger>
                      <SelectContent>
                        {q.options.map((_, i) => (
                          <SelectItem key={i} value={i.toString()}>
                            Option {i + 1}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  </div>
                ))}
              </div>
            </TabsContent>

            {/* Pronunciation Lesson Content */}
            <TabsContent value="pronunciation" className="space-y-4">
              <div className="flex items-center justify-between">
                <Label>Words to Practice</Label>
                <Button type="button" size="sm" onClick={addWord} variant="outline">
                  <Plus className="w-4 h-4 mr-1" />
                  Add Word
                </Button>
              </div>
              {words.map((word, idx) => (
                <div key={idx} className="grid grid-cols-3 gap-2 items-end">
                  <div className="space-y-1">
                    <Label className="text-xs">Word</Label>
                    <Input
                      placeholder="e.g., Restaurant"
                      value={word.word}
                      onChange={(e) => {
                        const newWords = [...words];
                        newWords[idx].word = e.target.value;
                        setWords(newWords);
                      }}
                    />
                  </div>
                  <div className="space-y-1">
                    <Label className="text-xs">Phonetic</Label>
                    <Input
                      placeholder="/ˈrestərɑːnt/"
                      value={word.phonetic}
                      onChange={(e) => {
                        const newWords = [...words];
                        newWords[idx].phonetic = e.target.value;
                        setWords(newWords);
                      }}
                    />
                  </div>
                  <Select
                    value={word.difficulty}
                    onValueChange={(v) => {
                      const newWords = [...words];
                      newWords[idx].difficulty = v;
                      setWords(newWords);
                    }}
                  >
                    <SelectTrigger>
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="easy">Easy</SelectItem>
                      <SelectItem value="medium">Medium</SelectItem>
                      <SelectItem value="hard">Hard</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
              ))}
            </TabsContent>

            {/* AI Conversation Content */}
            <TabsContent value="conversation" className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="scenario">Conversation Scenario</Label>
                <Textarea
                  id="scenario"
                  value={scenario}
                  onChange={(e) => setScenario(e.target.value)}
                  placeholder="e.g., Booking a hotel room - Practice making a reservation"
                  rows={3}
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="targetTurns">Target Number of Turns</Label>
                <Input
                  id="targetTurns"
                  type="number"
                  value={targetTurns}
                  onChange={(e) => setTargetTurns(e.target.value)}
                  min="5"
                  max="20"
                />
              </div>
            </TabsContent>

            {/* Flashcard Content */}
            <TabsContent value="flashcard" className="space-y-4">
              <div className="flex items-center justify-between">
                <Label>Flashcards</Label>
                <Button type="button" size="sm" onClick={addFlashcard} variant="outline">
                  <Plus className="w-4 h-4 mr-1" />
                  Add Card
                </Button>
              </div>
              {flashcards.map((card, idx) => (
                <div key={idx} className="border rounded-lg p-4 space-y-2">
                  <div className="grid grid-cols-2 gap-2">
                    <div className="space-y-1">
                      <Label className="text-xs">English Word</Label>
                      <Input
                        placeholder="e.g., Reservation"
                        value={card.word}
                        onChange={(e) => {
                          const newCards = [...flashcards];
                          newCards[idx].word = e.target.value;
                          setFlashcards(newCards);
                        }}
                      />
                    </div>
                    <div className="space-y-1">
                      <Label className="text-xs">Translation</Label>
                      <Input
                        placeholder="e.g., Đặt chỗ"
                        value={card.translation}
                        onChange={(e) => {
                          const newCards = [...flashcards];
                          newCards[idx].translation = e.target.value;
                          setFlashcards(newCards);
                        }}
                      />
                    </div>
                  </div>
                  <div className="space-y-1">
                    <Label className="text-xs">Example Sentence</Label>
                    <Input
                      placeholder="e.g., I made a reservation at the restaurant."
                      value={card.example}
                      onChange={(e) => {
                        const newCards = [...flashcards];
                        newCards[idx].example = e.target.value;
                        setFlashcards(newCards);
                      }}
                    />
                  </div>
                </div>
              ))}
            </TabsContent>
          </Tabs>

          <DialogFooter>
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
              Cancel
            </Button>
            <Button type="submit" className="bg-green-600 hover:bg-green-700 text-white">
              Create Lesson
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}