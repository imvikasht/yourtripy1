import React, { useState } from 'react';
import { ArrowLeft, Send, Sparkles, Eye, EyeOff, AlertCircle } from 'lucide-react';
import { supabase } from '../supabase';
import { ViewType, User } from '../App';

interface LoginPageProps {
  navigateTo: (view: ViewType) => void;
  onLogin: (user: User) => void;
}

const LoginPage: React.FC<LoginPageProps> = ({ navigateTo, onLogin }) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const handleForgotPassword = async () => {
    if (!email) {
      setError("Please enter your email address first to reset your password.");
      return;
    }
    
    setIsLoading(true);
    setError(null);
    setSuccessMessage(null);
    
    try {
      const { error: resetErr } = await supabase.auth.resetPasswordForEmail(email);
      if (resetErr) throw resetErr;
      setSuccessMessage("Password reset email sent! Please check your inbox.");
    } catch (err: any) {
      console.error("Password reset error:", err);
      setError(err.message || "Failed to send reset email. Please try again later.");
    } finally {
      setIsLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError(null);
    
    try {
      const { data, error: signInErr } = await supabase.auth.signInWithPassword({
        email,
        password,
      });

      if (signInErr) throw signInErr;

      const user = data.user;
      if (user) {
        onLogin({
          id: user.id,
          name: user.user_metadata?.name || user.email?.split('@')[0] || 'Explorer',
          email: user.email || '',
          avatar: user.user_metadata?.avatar_url || `https://ui-avatars.com/api/?name=${user.email}&background=FACC15&color=020617`
        });
      }
    } catch (err: any) {
      console.error("Login error:", err);
      setError(err.message || "Incorrect email or password. Please try again.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="pt-24 min-h-screen bg-brand-deep transition-colors flex flex-col justify-center items-center px-4 relative overflow-hidden">
      {/* Background Decor */}
      <div className="absolute top-0 right-0 w-[400px] h-[400px] bg-brand-primary/5 blur-[120px] rounded-full pointer-events-none"></div>
      <div className="absolute bottom-0 left-0 w-[400px] h-[400px] bg-brand-accent/5 blur-[120px] rounded-full pointer-events-none"></div>

      <div className="w-full max-w-md relative z-10">
        <button 
          onClick={() => navigateTo('home')}
          className="group flex items-center gap-2 text-brand-slate font-bold text-xs mb-8 hover:text-brand-primary transition-colors uppercase tracking-widest outline-none"
        >
          <ArrowLeft className="w-3.5 h-3.5 group-hover:-translate-x-1 transition-transform" />
          Return Home
        </button>

        <div className="bg-brand-dark rounded-[2.5rem] p-10 shadow-sm border border-brand-primary/10">
          <div className="flex justify-center mb-8">
            <div className="bg-brand-primary p-3 rounded-2xl shadow-xl shadow-brand-primary/20">
              <Sparkles className="text-white w-6 h-6 fill-current" />
            </div>
          </div>
          
          <h1 className="text-3xl font-black text-brand-heading text-center mb-2 tracking-tight">Welcome Back</h1>
          <p className="text-brand-slate text-sm text-center mb-8 font-medium">Log in to sync your AI co-pilot itineraries.</p>

          {error && (
            <div className="mb-6 p-4 bg-red-500/10 border border-red-500/20 rounded-2xl flex items-center gap-3 text-red-400 text-xs font-bold animate-in fade-in slide-in-from-top-2">
              <AlertCircle className="w-4 h-4 shrink-0" />
              {error}
            </div>
          )}

          {successMessage && (
            <div className="mb-6 p-4 bg-emerald-500/10 border border-emerald-500/20 rounded-2xl flex items-center gap-3 text-emerald-400 text-xs font-bold animate-in fade-in slide-in-from-top-2">
              <Sparkles className="w-4 h-4 shrink-0" />
              {successMessage}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="text-[10px] font-black uppercase tracking-widest text-brand-slate ml-1">Email Address</label>
              <input 
                type="email" 
                required 
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="explorer@yourtripy.com"
                className="w-full mt-2 px-5 py-4 rounded-2xl bg-brand-deep border border-brand-primary/10 text-brand-heading focus:border-brand-primary outline-none transition-all placeholder:text-brand-slate/40 font-medium" 
              />
            </div>

            <div>
              <div className="flex justify-between items-center ml-1">
                <label className="text-[10px] font-black uppercase tracking-widest text-brand-slate">Password</label>
                <button 
                  type="button" 
                  onClick={handleForgotPassword}
                  className="text-[10px] font-black text-brand-primary hover:underline uppercase tracking-widest"
                >
                  Forgot?
                </button>
              </div>
              <div className="relative mt-2">
                <input 
                  type={showPassword ? "text" : "password"} 
                  required 
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="••••••••"
                  className="w-full pl-5 pr-12 py-4 rounded-2xl bg-brand-deep border border-brand-primary/10 text-brand-heading focus:border-brand-primary outline-none transition-all placeholder:text-brand-slate/40 font-medium" 
                />
                <button 
                  type="button" 
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-4 top-1/2 -translate-y-1/2 text-brand-slate hover:text-brand-primary transition-colors focus:outline-none"
                >
                  {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                </button>
              </div>
            </div>

            <button 
              type="submit" 
              disabled={isLoading}
              className="w-full py-4 mt-2 rounded-2xl bg-brand-primary text-white font-black hover:bg-[#FF9A3C] transition-all flex items-center justify-center gap-2 shadow-xl shadow-brand-primary/20 active:scale-95 disabled:opacity-50"
            >
              {isLoading ? 'Signing in...' : 'Sign In'}
              <Send className="w-4 h-4" />
            </button>
          </form>



          <div className="mt-8 text-center text-xs text-brand-slate font-medium">
            Don't have an account?{' '}
            <button 
              onClick={() => navigateTo('signup')}
              className="text-brand-primary font-black hover:underline ml-1"
            >
              Sign Up
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
