import { useState } from 'react';
import { Card } from '@/app/components/ui/card';
import { Button } from '@/app/components/ui/button';
import { Input } from '@/app/components/ui/input';
import { Label } from '@/app/components/ui/label';
import { Textarea } from '@/app/components/ui/textarea';
import { Badge } from '@/app/components/ui/badge';
import {
  Sparkles,
  Video,
  Mic,
  MessageSquare,
  FileText,
  Plus,
  X,
  Upload,
  Save,
  Eye,
} from 'lucide-react';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/app/components/ui/select';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/app/components/ui/tabs';

export function CreateLesson() {
  const [lessonType, setLessonType] = useState<'video' | 'pronunciation' | 'conversation' | 'vocabulary'>('video');
  const [tags, setTags] = useState<string[]>([]);
  const [newTag, setNewTag] = useState('');

  const lessonTypes = [
    { value: 'video', label: 'Video Lesson', icon: Video, color: 'blue' },
    { value: 'pronunciation', label: 'Pronunciation Practice', icon: Mic, color: 'purple' },
    { value: 'conversation', label: 'AI Conversation', icon: MessageSquare, color: 'green' },
    { value: 'vocabulary', label: 'Vocabulary', icon: FileText, color: 'orange' },
  ];

  const addTag = () => {
    if (newTag && !tags.includes(newTag)) {
      setTags([...tags, newTag]);
      setNewTag('');
    }
  };

  const removeTag = (tag: string) => {
    setTags(tags.filter((t) => t !== tag));
  };

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-950 pb-20 md:pb-6">
      {/* Header */}
      <div className="bg-gradient-to-br from-green-400 to-green-600 text-white p-6 md:p-8">
        <div className="max-w-5xl mx-auto">
          <div className="flex items-center gap-3">
            <div className="bg-white/20 p-3 rounded-2xl backdrop-blur-sm">
              <Sparkles className="h-8 w-8" />
            </div>
            <div>
              <h1 className="text-3xl font-bold">Create New Lesson</h1>
              <p className="text-green-50">Design engaging learning experiences</p>
            </div>
          </div>
        </div>
      </div>

      {/* Content */}
      <div className="max-w-5xl mx-auto p-4 md:p-6">
        <Tabs defaultValue="basic" className="space-y-6">
          <TabsList className="grid w-full grid-cols-3">
            <TabsTrigger value="basic">Basic Info</TabsTrigger>
            <TabsTrigger value="content">Content</TabsTrigger>
            <TabsTrigger value="settings">Settings</TabsTrigger>
          </TabsList>

          {/* Basic Info Tab */}
          <TabsContent value="basic" className="space-y-6">
            <Card className="p-6">
              <h2 className="text-xl font-bold text-gray-900 dark:text-white mb-6">
                Lesson Information
              </h2>

              <div className="space-y-6">
                {/* Lesson Type */}
                <div>
                  <Label>Lesson Type</Label>
                  <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mt-2">
                    {lessonTypes.map((type) => {
                      const Icon = type.icon;
                      const isSelected = lessonType === type.value;
                      return (
                        <button
                          key={type.value}
                          onClick={() => setLessonType(type.value as any)}
                          className={`p-4 rounded-2xl border-2 transition-all ${
                            isSelected
                              ? 'border-green-500 bg-green-50 dark:bg-green-900/20'
                              : 'border-gray-200 dark:border-gray-700 hover:border-green-300'
                          }`}
                        >
                          <Icon
                            className={`h-8 w-8 mx-auto mb-2 ${
                              isSelected ? 'text-green-600' : 'text-gray-400'
                            }`}
                          />
                          <div className="text-sm font-semibold text-center">
                            {type.label.split(' ')[0]}
                          </div>
                        </button>
                      );
                    })}
                  </div>
                </div>

                {/* Title */}
                <div>
                  <Label htmlFor="title">Lesson Title *</Label>
                  <Input
                    id="title"
                    placeholder="Enter a clear, descriptive title"
                    className="mt-2"
                  />
                </div>

                {/* Description */}
                <div>
                  <Label htmlFor="description">Description</Label>
                  <Textarea
                    id="description"
                    placeholder="What will students learn in this lesson?"
                    className="mt-2 min-h-[100px]"
                  />
                </div>

                {/* Level & Duration */}
                <div className="grid md:grid-cols-2 gap-4">
                  <div>
                    <Label htmlFor="level">Level *</Label>
                    <Select>
                      <SelectTrigger className="mt-2">
                        <SelectValue placeholder="Select level" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="beginner">Beginner</SelectItem>
                        <SelectItem value="intermediate">Intermediate</SelectItem>
                        <SelectItem value="advanced">Advanced</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                  <div>
                    <Label htmlFor="duration">Duration (minutes) *</Label>
                    <Input
                      id="duration"
                      type="number"
                      placeholder="15"
                      className="mt-2"
                    />
                  </div>
                </div>

                {/* Tags */}
                <div>
                  <Label>Tags</Label>
                  <div className="flex gap-2 mt-2">
                    <Input
                      placeholder="Add a tag"
                      value={newTag}
                      onChange={(e) => setNewTag(e.target.value)}
                      onKeyPress={(e) => e.key === 'Enter' && addTag()}
                    />
                    <Button onClick={addTag} type="button">
                      <Plus className="h-4 w-4" />
                    </Button>
                  </div>
                  <div className="flex flex-wrap gap-2 mt-3">
                    {tags.map((tag) => (
                      <Badge
                        key={tag}
                        variant="outline"
                        className="gap-2 pr-1"
                      >
                        {tag}
                        <button
                          onClick={() => removeTag(tag)}
                          className="hover:text-red-500"
                        >
                          <X className="h-3 w-3" />
                        </button>
                      </Badge>
                    ))}
                  </div>
                </div>
              </div>
            </Card>
          </TabsContent>

          {/* Content Tab */}
          <TabsContent value="content" className="space-y-6">
            <Card className="p-6">
              <h2 className="text-xl font-bold text-gray-900 dark:text-white mb-6">
                Lesson Content
              </h2>

              <div className="space-y-6">
                {/* Upload Area */}
                <div className="border-2 border-dashed border-gray-300 dark:border-gray-700 rounded-2xl p-12 text-center hover:border-green-500 transition-colors cursor-pointer">
                  <Upload className="h-12 w-12 mx-auto text-gray-400 mb-4" />
                  <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-2">
                    Upload Content
                  </h3>
                  <p className="text-gray-600 dark:text-gray-400 mb-4">
                    Drag and drop files here or click to browse
                  </p>
                  <Button className="bg-green-500 hover:bg-green-600">
                    Choose Files
                  </Button>
                </div>

                {/* Content Editor */}
                <div>
                  <Label htmlFor="content">Lesson Script / Content</Label>
                  <Textarea
                    id="content"
                    placeholder="Enter the lesson content, dialogue, or instructions..."
                    className="mt-2 min-h-[300px] font-mono"
                  />
                </div>

                {/* Vocabulary Items (for vocabulary lessons) */}
                {lessonType === 'vocabulary' && (
                  <div>
                    <div className="flex items-center justify-between mb-4">
                      <Label>Vocabulary Items</Label>
                      <Button variant="outline" size="sm" className="gap-2">
                        <Plus className="h-4 w-4" />
                        Add Word
                      </Button>
                    </div>
                    <div className="space-y-3">
                      {[1, 2, 3].map((i) => (
                        <Card key={i} className="p-4">
                          <div className="grid md:grid-cols-3 gap-4">
                            <Input placeholder="Word" />
                            <Input placeholder="Definition" />
                            <Input placeholder="Example sentence" />
                          </div>
                        </Card>
                      ))}
                    </div>
                  </div>
                )}

                {/* Questions (for pronunciation/conversation) */}
                {(lessonType === 'pronunciation' || lessonType === 'conversation') && (
                  <div>
                    <div className="flex items-center justify-between mb-4">
                      <Label>Practice Questions</Label>
                      <Button variant="outline" size="sm" className="gap-2">
                        <Plus className="h-4 w-4" />
                        Add Question
                      </Button>
                    </div>
                    <div className="space-y-3">
                      {[1, 2].map((i) => (
                        <Card key={i} className="p-4">
                          <div className="space-y-3">
                            <Input placeholder="Question / Prompt" />
                            <Textarea
                              placeholder="Expected answer / Target pronunciation"
                              rows={2}
                            />
                          </div>
                        </Card>
                      ))}
                    </div>
                  </div>
                )}
              </div>
            </Card>
          </TabsContent>

          {/* Settings Tab */}
          <TabsContent value="settings" className="space-y-6">
            <Card className="p-6">
              <h2 className="text-xl font-bold text-gray-900 dark:text-white mb-6">
                Lesson Settings
              </h2>

              <div className="space-y-6">
                {/* Assignment */}
                <div>
                  <Label htmlFor="class">Assign to Class</Label>
                  <Select>
                    <SelectTrigger className="mt-2">
                      <SelectValue placeholder="Select class" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="10a">Class 10A</SelectItem>
                      <SelectItem value="10b">Class 10B</SelectItem>
                      <SelectItem value="11a">Class 11A</SelectItem>
                    </SelectContent>
                  </Select>
                </div>

                {/* XP Reward */}
                <div>
                  <Label htmlFor="xp">XP Reward</Label>
                  <Input
                    id="xp"
                    type="number"
                    placeholder="100"
                    className="mt-2"
                  />
                </div>

                {/* Passing Score */}
                <div>
                  <Label htmlFor="passing">Passing Score (%)</Label>
                  <Input
                    id="passing"
                    type="number"
                    placeholder="70"
                    className="mt-2"
                  />
                </div>

                {/* Max Attempts */}
                <div>
                  <Label htmlFor="attempts">Max Attempts</Label>
                  <Input
                    id="attempts"
                    type="number"
                    placeholder="3"
                    className="mt-2"
                  />
                </div>

                {/* Status */}
                <div>
                  <Label htmlFor="status">Status</Label>
                  <Select defaultValue="draft">
                    <SelectTrigger className="mt-2">
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="draft">Draft</SelectItem>
                      <SelectItem value="published">Published</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
              </div>
            </Card>
          </TabsContent>
        </Tabs>

        {/* Actions */}
        <Card className="p-6 mt-6">
          <div className="flex flex-wrap gap-3 justify-end">
            <Button variant="outline">Cancel</Button>
            <Button variant="outline" className="gap-2">
              <Eye className="h-4 w-4" />
              Preview
            </Button>
            <Button variant="outline" className="gap-2">
              <Save className="h-4 w-4" />
              Save Draft
            </Button>
            <Button className="bg-green-500 hover:bg-green-600 gap-2">
              <Sparkles className="h-4 w-4" />
              Publish Lesson
            </Button>
          </div>
        </Card>
      </div>
    </div>
  );
}
