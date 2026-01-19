import { Card } from '@/app/components/ui/card';
import { Button } from '@/app/components/ui/button';
import { FileText, Plus } from 'lucide-react';

export function CreateTest() {
  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-950 pb-20 md:pb-6">
      <div className="bg-gradient-to-br from-blue-400 to-blue-600 text-white p-6 md:p-8">
        <div className="max-w-5xl mx-auto">
          <div className="flex items-center gap-3">
            <div className="bg-white/20 p-3 rounded-2xl backdrop-blur-sm">
              <FileText className="h-8 w-8" />
            </div>
            <div>
              <h1 className="text-3xl font-bold">Create Test</h1>
              <p className="text-blue-50">Design assessments for your students</p>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-5xl mx-auto p-4 md:p-6">
        <Card className="p-12 text-center">
          <div className="flex flex-col items-center gap-4">
            <div className="bg-blue-100 dark:bg-blue-900/30 p-6 rounded-full">
              <FileText className="h-12 w-12 text-blue-600 dark:text-blue-400" />
            </div>
            <div>
              <h3 className="text-xl font-bold text-gray-900 dark:text-white mb-2">
                Test Creation Tool
              </h3>
              <p className="text-gray-600 dark:text-gray-400 mb-6">
                Coming soon in Sprint 2
              </p>
              <Button className="bg-blue-500 hover:bg-blue-600 gap-2">
                <Plus className="h-5 w-5" />
                Create Test
              </Button>
            </div>
          </div>
        </Card>
      </div>
    </div>
  );
}
