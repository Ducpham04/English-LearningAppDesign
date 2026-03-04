import { useState } from 'react';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogFooter } from '@/app/components/ui/dialog';
import { Button } from '@/app/components/ui/button';
import { Input } from '@/app/components/ui/input';
import { Label } from '@/app/components/ui/label';
import { Textarea } from '@/app/components/ui/textarea';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/app/components/ui/select';
import { Badge } from '@/app/components/ui/badge';
import { Copy, RefreshCw } from 'lucide-react';
import { toast } from 'sonner';
import { useAuthStore } from '@/store/authStore';
import apiService from '@/services/apiService';

interface CreateClassModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSuccess?: () => void;
}

export function CreateClassModal({ open, onOpenChange, onSuccess }: CreateClassModalProps) {
  const { user } = useAuthStore();
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    level: '',
    classCode: generateClassCode(),
    maxStudents: '25',
    schedule: [] as Array<{ day: string; time: string; duration: string }>,
  });

  const [currentSchedule, setCurrentSchedule] = useState({
    day: '',
    time: '',
    duration: '60',
  });

  const days = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];

  // Generate unique class code
  function generateClassCode() {
    const chars = 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789';
    let code = '';
    for (let i = 0; i < 6; i++) {
      code += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return code;
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!formData.name || !formData.level) {
      toast.error('Please fill in all required fields');
      return;
    }

    try {
      // Hiện tại ClassroomRequest yêu cầu: name, teacherId, startDate, endDate, status.
      // Ở đây ta chỉ gửi name, teacherId tạm thời; các trường khác BE có thể mặc định.
      const teacherId = user?.id ? Number(user.id) : 1;

      await apiService.classroom.create({
        name: formData.name,
        teacherId,
      });

      toast.success(`Class created successfully! Code: ${formData.classCode} 🎉`);
      onOpenChange(false);
      onSuccess?.();
      resetForm();
    } catch (error: any) {
      console.error('Failed to create classroom', error);
      toast.error(error?.message || 'Failed to create classroom');
    }
  };

  const resetForm = () => {
    setFormData({
      name: '',
      description: '',
      level: '',
      classCode: generateClassCode(),
      maxStudents: '25',
      schedule: [],
    });
    setCurrentSchedule({
      day: '',
      time: '',
      duration: '60',
    });
  };

  const copyClassCode = () => {
    navigator.clipboard.writeText(formData.classCode);
    toast.success('Class code copied to clipboard!');
  };

  const regenerateClassCode = () => {
    setFormData({ ...formData, classCode: generateClassCode() });
    toast.success('New class code generated!');
  };

  const addSchedule = () => {
    if (!currentSchedule.day || !currentSchedule.time) {
      toast.error('Please select day and time');
      return;
    }

    const exists = formData.schedule.some(
      s => s.day === currentSchedule.day && s.time === currentSchedule.time
    );

    if (exists) {
      toast.error('This schedule already exists');
      return;
    }

    setFormData({
      ...formData,
      schedule: [...formData.schedule, currentSchedule],
    });

    setCurrentSchedule({
      day: '',
      time: '',
      duration: '60',
    });
  };

  const removeSchedule = (index: number) => {
    setFormData({
      ...formData,
      schedule: formData.schedule.filter((_, i) => i !== index),
    });
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-2xl">
        <DialogHeader>
          <DialogTitle>Create New Class</DialogTitle>
          <DialogDescription>
            Set up a new class for your students.
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-6">
          {/* Basic Info */}
          <div className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="name">Class Name *</Label>
                <Input
                  id="name"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  placeholder="e.g., Beginner A1 - Morning"
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
                    <SelectItem value="beginner">Beginner (A1-A2)</SelectItem>
                    <SelectItem value="intermediate">Intermediate (B1-B2)</SelectItem>
                    <SelectItem value="advanced">Advanced (C1-C2)</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </div>

            {/* Class Code */}
            <div className="space-y-2">
              <Label htmlFor="classCode">Class Code (For Students to Join)</Label>
              <div className="flex gap-2">
                <div className="flex-1 relative">
                  <Input
                    id="classCode"
                    value={formData.classCode}
                    readOnly
                    className="font-mono text-lg font-bold text-center tracking-wider bg-gradient-to-r from-green-50 to-blue-50 dark:from-green-950 dark:to-blue-950"
                  />
                </div>
                <Button
                  type="button"
                  size="icon"
                  variant="outline"
                  onClick={copyClassCode}
                  title="Copy code"
                >
                  <Copy className="h-4 w-4" />
                </Button>
                <Button
                  type="button"
                  size="icon"
                  variant="outline"
                  onClick={regenerateClassCode}
                  title="Generate new code"
                >
                  <RefreshCw className="h-4 w-4" />
                </Button>
              </div>
              <p className="text-xs text-gray-500">
                Students can use this code to search and request to join this class
              </p>
            </div>

            <div className="space-y-2">
              <Label htmlFor="description">Description</Label>
              <Textarea
                id="description"
                value={formData.description}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                placeholder="Brief description of the class..."
                rows={3}
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="maxStudents">Maximum Students</Label>
              <Input
                id="maxStudents"
                type="number"
                value={formData.maxStudents}
                onChange={(e) => setFormData({ ...formData, maxStudents: e.target.value })}
                min="1"
                max="50"
              />
            </div>
          </div>

          {/* Schedule */}
          <div className="space-y-4">
            <Label>Class Schedule</Label>
            
            {/* Add Schedule */}
            <div className="border rounded-lg p-4 space-y-3 bg-gray-50 dark:bg-gray-800">
              <p className="text-sm font-medium">Add Schedule Slot:</p>
              <div className="grid grid-cols-4 gap-2">
                <Select value={currentSchedule.day} onValueChange={(value) => setCurrentSchedule({ ...currentSchedule, day: value })}>
                  <SelectTrigger>
                    <SelectValue placeholder="Day" />
                  </SelectTrigger>
                  <SelectContent>
                    {days.map((day) => (
                      <SelectItem key={day} value={day}>
                        {day}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>

                <Input
                  type="time"
                  value={currentSchedule.time}
                  onChange={(e) => setCurrentSchedule({ ...currentSchedule, time: e.target.value })}
                />

                <Select value={currentSchedule.duration} onValueChange={(value) => setCurrentSchedule({ ...currentSchedule, duration: value })}>
                  <SelectTrigger>
                    <SelectValue placeholder="Duration" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="30">30 min</SelectItem>
                    <SelectItem value="45">45 min</SelectItem>
                    <SelectItem value="60">60 min</SelectItem>
                    <SelectItem value="90">90 min</SelectItem>
                    <SelectItem value="120">120 min</SelectItem>
                  </SelectContent>
                </Select>

                <Button type="button" onClick={addSchedule} variant="outline">
                  Add
                </Button>
              </div>
            </div>

            {/* Current Schedules */}
            {formData.schedule.length > 0 && (
              <div className="border rounded-lg p-4 space-y-2">
                <p className="text-sm font-medium mb-2">
                  Current Schedule ({formData.schedule.length} slot{formData.schedule.length > 1 ? 's' : ''}):
                </p>
                <div className="space-y-2">
                  {formData.schedule.map((schedule, idx) => (
                    <div
                      key={idx}
                      className="flex items-center justify-between p-3 bg-white dark:bg-gray-900 rounded-lg"
                    >
                      <div className="flex items-center gap-3">
                        <Badge variant="outline">{schedule.day}</Badge>
                        <span className="text-sm font-medium">{schedule.time}</span>
                        <span className="text-sm text-gray-500">
                          ({schedule.duration} minutes)
                        </span>
                      </div>
                      <Button
                        type="button"
                        size="sm"
                        variant="ghost"
                        onClick={() => removeSchedule(idx)}
                      >
                        Remove
                      </Button>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>

          {/* Summary */}
          <div className="bg-blue-50 dark:bg-blue-900/20 rounded-lg p-4">
            <h4 className="font-semibold mb-2">Class Summary:</h4>
            <ul className="space-y-1 text-sm">
              <li>• Class Code: <strong className="font-mono">{formData.classCode}</strong></li>
              <li>• Level: <strong>{formData.level || 'Not selected'}</strong></li>
              <li>• Max Students: <strong>{formData.maxStudents}</strong></li>
              <li>• Schedule Slots: <strong>{formData.schedule.length}</strong></li>
              <li>
                • Weekly Hours: <strong>
                  {formData.schedule.reduce((sum, s) => sum + parseInt(s.duration), 0) / 60} hours
                </strong>
              </li>
            </ul>
          </div>

          <DialogFooter>
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
              Cancel
            </Button>
            <Button type="submit" className="bg-green-600 hover:bg-green-700 text-white">
              Create Class
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}