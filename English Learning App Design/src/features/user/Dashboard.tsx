import { Button } from '@/app/components/ui/button';
import { Card } from '@/app/components/ui/card';
import { GamificationBar } from '@/features/gamification/GamificationBar';
import { SessionCard } from '@/features/session/SessionCard';
import { FloatingParticles } from '@/shared/ui/FloatingParticles';
import { AnimatedMascot } from '@/shared/ui/AnimatedMascot';
import { useAppStore } from '@/store/appStore';
import { useStudentDashboard } from '@/hooks/useStudentData';
import { PlayCircle, BookOpen, Award, Sparkles, Zap, Target, Users, Calendar, GraduationCap } from 'lucide-react';
import { motion } from 'motion/react';
import { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import apiService from '@/services/apiService';
import type { Session } from '@/types';

export function Dashboard() {
  const { user, sessions, userProgress, setCurrentView, setSelectedSession, setSessions, hydrateFromDashboard } = useAppStore();
  const navigate = useNavigate();
  const { data, loading } = useStudentDashboard();
  const hydratedRef = useRef(false);

  const [showMascot, setShowMascot] = useState(true);

  // Hydrate user from API and load participated sessions (đã/đang tham gia) cho "Đang học"
  useEffect(() => {
    if (!data || hydratedRef.current) return;
    hydratedRef.current = true;
    if (data.user) {
      hydrateFromDashboard({
        user: {
          id: String(data.user.id),
          fullName: data.user.fullName,
          avatarUrl: data.user.avatarUrl,
          level: data.user.level ?? 1,
          xp: data.user.xp ?? 0,
          streak: data.user.streak ?? 0,
          totalLessonsCompleted: data.user.totalLessonsCompleted ?? 0,
        },
      });
    }
    // Load sessions mà user đã/đang tham gia (có ít nhất 1 bài hoàn thành)
    apiService.student
      .getParticipatedSessions()
      .then((res) => {
        const list = res.data?.data ?? [];
        const participated: Session[] = (Array.isArray(list) ? list : []).map((s: any) => ({
          id: String(s.sessionId),
          title: s.title || '',
          description: s.topicName ? `Chủ đề: ${s.topicName}` : '',
          level: 'beginner' as const,
          totalLessons: s.totalLessons ?? 0,
          completedLessons: s.completedLessons ?? 0,
          estimatedTime: (s.totalLessons ?? 0) * 5,
          status: s.sessionCompleted ? ('completed' as const) : ('in-progress' as const),
          order: 0,
        }));
        participated.sort((a, b) => (b.completedLessons / Math.max(1, b.totalLessons)) - (a.completedLessons / Math.max(1, a.totalLessons)));
        setSessions(participated);
      })
      .catch(() => setSessions([]));
  }, [data, hydrateFromDashboard, setSessions]);

  const enrolledClasses = data?.enrolledClasses ?? [];
  const currentSession = sessions.find(s => s.status === 'in-progress') || sessions[0];
  const inProgressSessions = sessions;

  const getMascotMessage = () => {
    if (!user) return 'Chào bạn! Hôm nay chúng ta học gì nào? 😊';
    if (user.streak >= 7) return 'Wow! Bạn đã học liên tục 7 ngày! Tuyệt vời! 🔥';
    if (user.totalLessonsCompleted >= 10) return 'Bạn đã hoàn thành nhiều bài học rồi đấy! Tiếp tục phát huy nhé! 💪';
    return 'Chào bạn! Hôm nay chúng ta học gì nào? 😊';
  };

  if (loading && !user) {
    return (
      <div className="container mx-auto px-4 py-8 flex items-center justify-center min-h-[50vh]">
        <p className="text-gray-500">Đang tải...</p>
      </div>
    );
  }

  const displayUser = user ?? { id: '', name: 'Học viên', avatar: undefined, level: 1, xp: 0, streak: 0, totalLessonsCompleted: 0 };

  return (
    <div className="relative min-h-screen">
      {/* Floating Particles Background */}
      <FloatingParticles count={15} />
      
      {/* Animated Mascot */}
        {showMascot && (
        <AnimatedMascot
          mood={displayUser.streak >= 7 ? 'celebrating' : 'happy'}
          message={getMascotMessage()}
          position="bottom-right"
        />
      )}

      <div className="container mx-auto px-4 py-8 relative z-10">
        {/* Welcome Section - Duolingo Style */}
        <motion.div
          className="mb-8 relative overflow-hidden rounded-3xl shadow-lg"
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
        >
          <div className="absolute inset-0 bg-gradient-to-br from-green-400 via-green-500 to-green-600" />
          <div className="relative p-8 md:p-12 text-white">
            <motion.div
              initial={{ scale: 0 }}
              animate={{ scale: 1 }}
              transition={{ delay: 0.3, type: 'spring', stiffness: 200 }}
              className="inline-block mb-4"
            >
              <div className="text-6xl">👋</div>
            </motion.div>
            <motion.h1
              className="mb-3 text-4xl md:text-5xl font-bold"
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: 0.4 }}
            >
              Xin chào, {displayUser.name}!
            </motion.h1>
            <motion.p
              className="text-lg md:text-xl opacity-90 mb-6"
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: 0.5 }}
            >
              Hãy tiếp tục hành trình chinh phục tiếng Anh của bạn 🚀
            </motion.p>
            
            {/* Daily streak celebration */}
            {displayUser.streak > 0 && (
              <motion.div
                initial={{ opacity: 0, scale: 0.8 }}
                animate={{ opacity: 1, scale: 1 }}
                transition={{ delay: 0.6 }}
                className="inline-flex items-center gap-2 bg-white/20 backdrop-blur-sm rounded-2xl px-6 py-3 border-2 border-white/30"
              >
                <Sparkles className="h-5 w-5 text-yellow-300" />
                <span className="font-bold">{displayUser.streak} ngày streak!</span>
                <div className="text-2xl">🔥</div>
              </motion.div>
            )}
          </div>
        </motion.div>

        {/* Gamification Stats */}
        <motion.div
          className="mb-8"
          initial={{ opacity: 0, scale: 0.95 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ duration: 0.5, delay: 0.2 }}
        >
          <GamificationBar />
        </motion.div>

        {/* My Classes Section */}
        {enrolledClasses.length > 0 && (
          <motion.div
            className="mb-8"
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: 0.25 }}
          >
            <div className="mb-6 flex items-center justify-between">
              <h2 className="text-2xl font-bold flex items-center gap-2">
                <div className="flex h-10 w-10 items-center justify-center rounded-2xl bg-blue-100 dark:bg-blue-900/30">
                  <GraduationCap className="h-6 w-6 text-blue-600 dark:text-blue-400" />
                </div>
                Lớp học của tôi
              </h2>
            </div>
            <div className="grid gap-4 sm:grid-cols-2">
              {enrolledClasses.map((classItem: any, index: number) => {
                const item = {
                  id: String(classItem.id),
                  name: classItem.name ?? '',
                  description: classItem.description ?? '',
                  teacherName: classItem.teacherName ?? '',
                  studentCount: classItem.studentCount ?? 0,
                  level: (classItem.level ?? 'beginner') as 'beginner' | 'intermediate' | 'advanced',
                  schedule: classItem.startDate ? [classItem.startDate, classItem.endDate].filter(Boolean).join(' → ') : undefined,
                };
                return (
                <motion.div
                  key={item.id}
                  initial={{ opacity: 0, scale: 0.9 }}
                  animate={{ opacity: 1, scale: 1 }}
                  transition={{ delay: 0.3 + index * 0.1 }}
                  whileHover={{ y: -4, transition: { duration: 0.2 } }}
                >
                  <Card className="group cursor-pointer overflow-hidden rounded-3xl border-2 border-gray-200 dark:border-gray-700 hover:border-blue-300 dark:hover:border-blue-600 transition-all hover:shadow-xl">
                    <div className={`h-3 bg-gradient-to-r ${(classItem as any).color || 'from-blue-400 to-blue-500'}`} />
                    <div className="p-6">
                      <div className="mb-4">
                        <div className="flex items-start justify-between mb-2">
                          <h3 className="text-lg font-bold text-gray-900 dark:text-white group-hover:text-blue-600 dark:group-hover:text-blue-400 transition-colors">
                            {item.name}
                          </h3>
                          <span className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-medium ${
                            item.level === 'beginner' 
                              ? 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400'
                              : item.level === 'intermediate'
                              ? 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400'
                              : 'bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-400'
                          }`}>
                            {item.level === 'beginner' ? 'Cơ bản' : item.level === 'intermediate' ? 'Trung cấp' : 'Nâng cao'}
                          </span>
                        </div>
                        <p className="text-sm text-gray-600 dark:text-gray-400 line-clamp-2">
                          {item.description}
                        </p>
                      </div>

                      <div className="space-y-3">
                        <div className="flex items-center gap-3">
                          <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-gradient-to-br from-blue-400 to-blue-500 text-white font-bold shadow-md">
                            {item.teacherName.charAt(0)}
                          </div>
                          <div className="flex-1 min-w-0">
                            <p className="text-xs text-gray-500 dark:text-gray-400">Giáo viên</p>
                            <p className="text-sm font-semibold text-gray-900 dark:text-white truncate">
                              {item.teacherName}
                            </p>
                          </div>
                        </div>

                        <div className="flex items-center gap-4 text-sm text-gray-600 dark:text-gray-400">
                          <div className="flex items-center gap-2">
                            <div className="flex h-8 w-8 items-center justify-center rounded-xl bg-green-100 dark:bg-green-900/30">
                              <Users className="h-4 w-4 text-green-600 dark:text-green-400" />
                            </div>
                            <span className="font-medium">{item.studentCount} học viên</span>
                          </div>
                          {item.schedule && (
                            <>
                              <div className="h-4 w-px bg-gray-300 dark:bg-gray-600" />
                              <div className="flex items-center gap-2">
                                <Calendar className="h-4 w-4" />
                                <span className="text-xs">{item.schedule}</span>
                              </div>
                            </>
                          )}
                        </div>
                      </div>
                    </div>
                  </Card>
                </motion.div>
                );
              })}
            </div>
          </motion.div>
        )}

        {/* Continue Learning - Duolingo Style */}
        {currentSession && (
          <motion.div
            className="mb-8"
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: 0.3 }}
          >
            <Card className="relative overflow-hidden group cursor-pointer transition-all hover:shadow-2xl rounded-3xl border-2 border-green-200 dark:border-green-800">
              <div className="absolute inset-0 bg-gradient-to-br from-green-50 to-green-100 dark:from-green-900/20 dark:to-green-800/20" />
              
              {/* Animated glow effect */}
              <motion.div
                className="absolute inset-0 bg-gradient-to-r from-transparent via-green-200/30 to-transparent"
                animate={{
                  x: ['-100%', '100%'],
                }}
                transition={{
                  repeat: Infinity,
                  duration: 3,
                  ease: 'linear',
                }}
              />

              <div className="relative p-6 md:p-8">
                <div className="flex items-center gap-2 mb-3">
                  <div className="flex h-12 w-12 items-center justify-center rounded-2xl bg-green-500 text-white shadow-lg">
                    <Zap className="h-6 w-6" />
                  </div>
                  <h2 className="text-2xl font-bold text-green-700 dark:text-green-400">Tiếp tục học</h2>
                </div>
                <div className="mb-6">
                  <h3 className="mb-2 text-xl font-semibold">{currentSession.title}</h3>
                  <p className="text-gray-600 dark:text-gray-300">{currentSession.description}</p>
                  <div className="mt-4 flex items-center gap-4 text-sm text-gray-600 dark:text-gray-400">
                    <div className="flex items-center gap-2">
                      <div className="flex h-8 w-8 items-center justify-center rounded-xl bg-green-100 dark:bg-green-900/30">
                        <Target className="h-4 w-4 text-green-600 dark:text-green-400" />
                      </div>
                      <span className="font-medium">{currentSession.completedLessons}/{currentSession.totalLessons} bài</span>
                    </div>
                    <div className="h-4 w-px bg-gray-300 dark:bg-gray-600" />
                    <span>~{currentSession.estimatedTime} phút</span>
                  </div>
                </div>
                <motion.div
                  whileHover={{ scale: 1.05 }}
                  whileTap={{ scale: 0.95 }}
                >
                  <Button
                    size="lg"
                    className="bg-green-500 hover:bg-green-600 text-white shadow-lg gap-2 rounded-2xl font-bold text-base"
                    onClick={() => {
                      console.log('Dashboard - Continue button clicked');
                      if (userProgress.currentLessonId) {
                        navigate(`/lessons/${userProgress.currentLessonId}`);
                      } else {
                        setSelectedSession(currentSession.id);
                        navigate(`/sessions/${currentSession.id}`);
                      }
                    }}
                  >
                    <PlayCircle className="h-5 w-5" />
                    Tiếp tục ngay
                  </Button>
                </motion.div>
              </div>
            </Card>
          </motion.div>
        )}

        {/* Quick Actions - Duolingo Style with rounded icons */}
        <motion.div
          className="mb-8 grid gap-4 sm:grid-cols-3"
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.4 }}
        >
          {[
            {
              icon: BookOpen,
              title: 'Chủ đề',
              description: 'Học theo chủ đề và session',
              gradient: 'from-green-400 to-green-500',
              bgColor: 'bg-green-100 dark:bg-green-900/30',
              iconColor: 'text-green-600 dark:text-green-400',
              view: 'sessions' as const,
            },
            {
              icon: BookOpen,
              title: 'Từ vựng',
              description: 'Ôn tập từ vựng đã học',
              gradient: 'from-green-500 to-green-600',
              bgColor: 'bg-green-100 dark:bg-green-900/30',
              iconColor: 'text-green-600 dark:text-green-400',
              view: 'vocabulary' as const,
            },
            {
              icon: Award,
              title: 'Thành tích',
              description: 'Xem huy hiệu và thành tích',
              gradient: 'from-yellow-400 to-yellow-500',
              bgColor: 'bg-yellow-100 dark:bg-yellow-900/30',
              iconColor: 'text-yellow-600 dark:text-yellow-400',
              view: 'profile' as const,
            },
          ].map((action, index) => (
            <motion.div
              key={action.title}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.5 + index * 0.1 }}
              whileHover={{ y: -8, transition: { duration: 0.2 } }}
            >
              <Card
                className="cursor-pointer p-6 transition-all hover:shadow-xl group relative overflow-hidden rounded-3xl border-2 hover:border-green-300 dark:hover:border-green-700"
                onClick={() => {
                  if (action.view === 'sessions') {
                    navigate('/sessions');
                  } else if (action.view === 'vocabulary') {
                    navigate('/vocabulary');
                  } else if (action.view === 'profile') {
                    navigate('/profile');
                  }
                }}
              >
                {/* Gradient overlay on hover */}
                <div className={`absolute inset-0 bg-gradient-to-br ${action.gradient} opacity-0 group-hover:opacity-10 transition-opacity`} />
                
                <motion.div
                  className={`mb-4 flex h-16 w-16 items-center justify-center rounded-3xl ${action.bgColor} shadow-md`}
                  whileHover={{ rotate: [0, -10, 10, -10, 0] }}
                  transition={{ duration: 0.5 }}
                >
                  <action.icon className={`h-8 w-8 ${action.iconColor}`} />
                </motion.div>
                <h3 className="mb-2 font-bold text-lg">{action.title}</h3>
                <p className="text-sm text-gray-600 dark:text-gray-400">
                  {action.description}
                </p>
              </Card>
            </motion.div>
          ))}
        </motion.div>

        {/* In Progress Sessions */}
        {inProgressSessions.length > 0 && (
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: 0.6 }}
          >
            <h2 className="mb-6 text-2xl font-bold flex items-center gap-2">
              <div className="flex h-10 w-10 items-center justify-center rounded-2xl bg-green-100 dark:bg-green-900/30">
                <Sparkles className="h-6 w-6 text-green-600 dark:text-green-400" />
              </div>
              Đang học / Đã tham gia
            </h2>
            <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
              {inProgressSessions.map((session, index) => (
                <motion.div
                  key={session.id}
                  initial={{ opacity: 0, scale: 0.9 }}
                  animate={{ opacity: 1, scale: 1 }}
                  transition={{ delay: 0.7 + index * 0.1 }}
                  whileHover={{ scale: 1.05 }}
                >
                  <SessionCard
                    session={session}
                    onClick={() => {
                      setSelectedSession(session.id);
                      navigate(`/sessions/${session.id}`);
                    }}
                  />
                </motion.div>
              ))}
            </div>
          </motion.div>
        )}
      </div>
    </div>
  );
}