import { useEffect, useMemo, useState } from 'react';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogFooter } from '@/app/components/ui/dialog';
import { Button } from '@/app/components/ui/button';
import { Label } from '@/app/components/ui/label';
import { Checkbox } from '@/app/components/ui/checkbox';
import { Calendar } from '@/app/components/ui/calendar';
import { Popover, PopoverContent, PopoverTrigger } from '@/app/components/ui/popover';
import { CalendarIcon, Users } from 'lucide-react';
import { toast } from 'sonner';
import { format } from 'date-fns';
import apiService from '@/services/apiService';

interface AssignContentModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  contentType: 'lesson' | 'test' | 'session';
  contentId?: string;
  onSuccess?: () => void;
}

export function AssignContentModal({ 
  open, 
  onOpenChange, 
  contentType,
  contentId,
  onSuccess 
}: AssignContentModalProps) {
  const [selectedClasses, setSelectedClasses] = useState<string[]>([]);
  const [dueDate, setDueDate] = useState<Date>();
  // Only classes assignment is implemented with real API right now.

  const [classes, setClasses] = useState<Array<{ id: string; name: string; students: number }>>([]);
  const [submitting, setSubmitting] = useState(false);
  const [loadingClasses, setLoadingClasses] = useState(false);

  useEffect(() => {
    if (!open) return;
    let cancelled = false;
    setLoadingClasses(true);
    apiService.teacherClasses.getAll()
      .then((res) => {
        const payload = res.data?.data ?? res.data ?? [];
        const arr = Array.isArray(payload) ? payload : [];
        if (cancelled) return;
        setClasses(arr.map((c: any) => ({
          id: String(c.id),
          name: c.name || `Lớp ${c.id}`,
          students: c.studentCount ?? 0,
        })));
      })
      .catch((err: any) => {
        const msg = err?.response?.data?.message ?? err?.message ?? 'Không tải được danh sách lớp';
        toast.error(msg);
        setClasses([]);
      })
      .finally(() => {
        if (!cancelled) setLoadingClasses(false);
      });
    return () => { cancelled = true; };
  }, [open]);

  const totalRecipients = useMemo(() => {
    return selectedClasses.reduce((sum, classId) => {
      const cls = classes.find(c => c.id === classId);
      return sum + (cls?.students || 0);
    }, 0);
  }, [selectedClasses, classes]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (selectedClasses.length === 0) {
      toast.error('Please select at least one class');
      return;
    }

    if (!contentId) {
      toast.error('Missing contentId');
      return;
    }
    if (contentType !== 'lesson') {
      toast.error('Only lesson assignment is supported right now');
      return;
    }
    const lessonId = Number(contentId);
    if (Number.isNaN(lessonId)) {
      toast.error('Invalid lessonId');
      return;
    }

    try {
      setSubmitting(true);
      await Promise.all(
        selectedClasses.map((classId) =>
          apiService.teacherClasses.createAssignment(Number(classId), {
            lessonId,
            dueAt: dueDate ? dueDate.toISOString() : undefined,
          })
        )
      );
    } catch (err: any) {
      const msg = err?.response?.data?.message ?? err?.message ?? 'Assign failed';
      toast.error(msg);
      return;
    } finally {
      setSubmitting(false);
    }

    toast.success(
      `${contentType.charAt(0).toUpperCase() + contentType.slice(1)} assigned to ${totalRecipients} student${totalRecipients > 1 ? 's' : ''} successfully!`
    );
    
    onOpenChange(false);
    onSuccess?.();
    resetForm();
  };

  const resetForm = () => {
    setSelectedClasses([]);
    setDueDate(undefined);
  };

  const toggleClass = (classId: string) => {
    setSelectedClasses(prev =>
      prev.includes(classId)
        ? prev.filter(id => id !== classId)
        : [...prev, classId]
    );
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>
            Assign {contentType === 'session' ? 'Session' : contentType === 'lesson' ? 'Lesson' : 'Test'} to Class
          </DialogTitle>
          <DialogDescription>
            Select a class and set the deadline for this assignment.
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-6">
          {/* Classes Selection */}
          <div className="space-y-3">
            <Label>Select Classes</Label>
            <div className="border rounded-lg divide-y max-h-64 overflow-y-auto">
              {loadingClasses && (
                <div className="p-3 text-sm text-muted-foreground">Loading classes...</div>
              )}
              {classes.map((cls) => (
                <div
                  key={cls.id}
                  className="flex items-center gap-3 p-3 hover:bg-gray-50 dark:hover:bg-gray-800"
                >
                  <Checkbox
                    id={`class-${cls.id}`}
                    checked={selectedClasses.includes(cls.id)}
                    onCheckedChange={() => toggleClass(cls.id)}
                  />
                  <label
                    htmlFor={`class-${cls.id}`}
                    className="flex-1 cursor-pointer"
                  >
                    <p className="font-medium">{cls.name}</p>
                    <p className="text-sm text-gray-500">
                      {cls.students} students
                    </p>
                  </label>
                </div>
              ))}
              {!loadingClasses && classes.length === 0 && (
                <div className="p-3 text-sm text-muted-foreground">No classes found</div>
              )}
            </div>
            {selectedClasses.length > 0 && (
              <p className="text-sm text-green-600 dark:text-green-400">
                {totalRecipients} students will receive this {contentType}
              </p>
            )}
          </div>

          {/* Due Date */}
          <div className="space-y-2">
            <Label>Due Date (Optional)</Label>
            <Popover>
              <PopoverTrigger asChild>
                <Button
                  variant="outline"
                  className="w-full justify-start text-left font-normal"
                >
                  <CalendarIcon className="mr-2 h-4 w-4" />
                  {dueDate ? format(dueDate, 'PPP') : <span>Pick a due date</span>}
                </Button>
              </PopoverTrigger>
              <PopoverContent className="w-auto p-0">
                <Calendar
                  mode="single"
                  selected={dueDate}
                  onSelect={setDueDate}
                  initialFocus
                />
              </PopoverContent>
            </Popover>
          </div>

          {/* Summary */}
          <div className="bg-blue-50 dark:bg-blue-900/20 rounded-lg p-4">
            <h4 className="font-semibold mb-2">Assignment Summary:</h4>
            <ul className="space-y-1 text-sm">
              <li>
                • Content Type: <strong>{contentType.charAt(0).toUpperCase() + contentType.slice(1)}</strong>
              </li>
              <li>
                • Assigned to: <strong>
                  {`${selectedClasses.length} class${selectedClasses.length !== 1 ? 'es' : ''}`}
                </strong>
              </li>
              {dueDate && (
                <li>
                  • Due Date: <strong>{format(dueDate, 'PPP')}</strong>
                </li>
              )}
            </ul>
          </div>

          <DialogFooter>
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
              Cancel
            </Button>
            <Button 
              type="submit" 
              className="bg-green-600 hover:bg-green-700 text-white"
              disabled={
                submitting ||
                selectedClasses.length === 0
              }
            >
              {submitting ? 'Assigning...' : `Assign ${contentType.charAt(0).toUpperCase() + contentType.slice(1)}`}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}