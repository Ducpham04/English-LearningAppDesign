import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button } from '@/app/components/ui/button';
import { Card } from '@/app/components/ui/card';
import { Input } from '@/app/components/ui/input';
import { Label } from '@/app/components/ui/label';
import { motion } from 'motion/react';
import { Mail, Lock, AlertCircle, Loader2 } from 'lucide-react';
import { useAuthStore } from '@/store/authStore';
import { getErrorMessage, logError } from '@/utils/errorHandler';

export function Login() {
  const navigate = useNavigate();
  const { login, isLoading, user, isAuthenticated } = useAuthStore();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  // Redirect if already authenticated
  useEffect(() => {
    if (isAuthenticated && user) {
      console.log('[Login] User already authenticated, redirecting to dashboard:', user.role);
      const dashboardRoute = user.role === 'teacher' ? '/teacher' : user.role === 'admin' ? '/admin' : '/';
      navigate(dashboardRoute, { replace: true });
    }
  }, [isAuthenticated, user, navigate]);

  // Navigate when user is set after login
  useEffect(() => {
    console.log('[Login] useEffect triggered, user:', user, 'navigate:', navigate);
    if (user && !email) {  // Only navigate if login was just completed (no email in form)
      console.log('[Login] User found, navigating to role:', user.role);
      // Determine dashboard route based on user role
      let dashboardRoute = '/';
      if (user.role === 'teacher') {
        dashboardRoute = '/teacher';
      } else if (user.role === 'admin') {
        dashboardRoute = '/admin';
      }
      
      // Navigate to appropriate dashboard
      navigate(dashboardRoute, { replace: true });
    }
  }, [user, navigate, email]);

  const handleLogin = async () => {
    setError('');
    if (!email || !password) {
      setError('Please enter both email and password');
      return;
    }
    
    try {
      console.log('[Login] Attempting login for:', email);
      await login({ email, password });
      console.log('[Login] Login successful, state should update user');
    } catch (err: any) {
      logError('Login', err, { email });
      const errorMsg = getErrorMessage(err, 'Login failed');
      setError(errorMsg);
    }
  };

  const demoUsers = [
    { role: 'student', email: 'student@demo.com', color: 'bg-green-500' },
    { role: 'teacher', email: 'teacher@demo.com', color: 'bg-blue-500' },
    { role: 'admin', email: 'admin@demo.com', color: 'bg-purple-500' },
  ];

  return (
    <div className="min-h-screen bg-gradient-to-br from-green-50 via-blue-50 to-purple-50 dark:from-gray-900 dark:via-gray-900 dark:to-gray-900 flex items-center justify-center p-4">
      <div className="max-w-5xl w-full">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5 }}
          className="text-center mb-8"
        >
          <div className="flex justify-center mb-6">
            <div className="flex h-16 w-16 items-center justify-center rounded-3xl bg-gradient-to-br from-green-400 to-green-600 text-white font-bold shadow-2xl text-3xl">
              E
            </div>
          </div>
          <h1 className="text-3xl md:text-4xl font-bold text-gray-900 dark:text-white mb-2">
            Welcome back!
          </h1>
          <p className="text-lg text-gray-600 dark:text-gray-300">
            Sign in to continue your learning journey
          </p>
        </motion.div>

        <div className="grid md:grid-cols-2 gap-8">
          {/* Demo Users */}
          <motion.div
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: 0.3 }}
          >
            <h2 className="text-xl font-bold text-gray-900 dark:text-white mb-4">
              Quick Login Demo
            </h2>
            <div className="space-y-3">
              {demoUsers.map((demo, index) => (
                <Card
                  key={demo.role}
                  className="p-4 cursor-pointer transition-all duration-300 hover:shadow-lg hover:border-green-500"
                  onClick={() => {
                    setEmail(demo.email);
                    setPassword('password123');
                  }}
                >
                  <div className="flex items-center gap-3">
                    <div className={`h-10 w-10 rounded-full flex items-center justify-center text-white font-bold ${demo.color}`}>
                      {demo.role[0].toUpperCase()}
                    </div>
                    <div className="flex-1">
                      <h3 className="font-semibold text-gray-900 dark:text-white capitalize">{demo.role}</h3>
                      <p className="text-sm text-gray-500 dark:text-gray-400">{demo.email}</p>
                    </div>
                  </div>
                </Card>
              ))}
            </div>
          </motion.div>

          {/* Login Form */}
          <motion.div
            initial={{ opacity: 0, x: 20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: 0.3 }}
          >
            <Card className="p-8">
              <form onSubmit={(e) => { e.preventDefault(); handleLogin(); }} className="space-y-6">
                <div>
                  <Label htmlFor="email">Email</Label>
                  <div className="relative mt-2">
                    <Mail className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-400" />
                    <Input
                      id="email"
                      type="email"
                      placeholder="your@email.com"
                      value={email}
                      onChange={(e) => setEmail(e.target.value)}
                      className="pl-10 rounded-xl"
                      disabled={isLoading}
                    />
                  </div>
                </div>

                <div>
                  <Label htmlFor="password">Password</Label>
                  <div className="relative mt-2">
                    <Lock className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-400" />
                    <Input
                      id="password"
                      type="password"
                      placeholder="Enter your password"
                      value={password}
                      onChange={(e) => setPassword(e.target.value)}
                      className="pl-10 rounded-xl"
                      disabled={isLoading}
                    />
                  </div>
                </div>

                {error && (
                  <motion.div
                    initial={{ opacity: 0, y: -10 }}
                    animate={{ opacity: 1, y: 0 }}
                    className="flex items-center gap-2 p-3 bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-xl text-red-600 dark:text-red-400"
                  >
                    <AlertCircle className="h-5 w-5 flex-shrink-0" />
                    <p className="text-sm">{error}</p>
                  </motion.div>
                )}

                <Button
                  size="lg"
                  disabled={isLoading}
                  className="w-full bg-green-500 hover:bg-green-600 text-white rounded-xl py-6 text-lg shadow-lg hover:shadow-xl transition-all"
                  onClick={handleLogin}
                >
                  {isLoading ? (
                    <>
                      <Loader2 className="mr-2 h-5 w-5 animate-spin" />
                      Signing in...
                    </>
                  ) : (
                    'Sign In'
                  )}
                </Button>

                <div className="text-center">
                  <button
                    type="button"
                    onClick={() => navigate('/register')}
                    className="text-green-600 dark:text-green-400 hover:underline"
                  >
                    Don't have an account? Register
                  </button>
                </div>
              </form>
            </Card>
          </motion.div>
        </div>
      </div>
    </div>
  );
}
