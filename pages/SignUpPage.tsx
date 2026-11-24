import React, { useState } from 'react';
import { ArrowLeft, Send, Sparkles, User as UserIcon, Mail, Lock, AlertCircle } from 'lucide-react';
import { supabase } from '../supabase';
import { ViewType, User } from '../App';

interface SignUpPageProps {
  navigateTo: (view: ViewType) => void;
  onLogin: (user: User) => void;
}

const SignUpPage: React.FC<SignUpPageProps> = ({ navigateTo, onLogin }) => {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError(null);
    
    try {
      const { data, error: signUpErr } = await supabase.auth.signUp({
        email,
        password,
        options: {
          data: {
            name: name,
            avatar_url: `https://ui-avatars.com/api/?name=${encodeURIComponent(name)}&background=FACC15&color=020617`
          }
        }
      });

      if (signUpErr) throw signUpErr;

      const user = data.user;
      if (user) {
        onLogin({
          id: user.id,
          name: name,
          email: email,
          avatar: `https://ui-avatars.com/api/?name=${encodeURIComponent(name)}&background=FACC15&color=020617`
        });
      }
    } catch (err: any) {
      console.error("Signup error:", err);
      setError(err.message || "Failed to create account. Please try again.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="pt-24 min-h-screen bg-brand-deep transition-colors flex flex-col justify-center items-center px-4 relative overflow-hidden">
      {/* Background Decor */}
      <div className="absolute top-0 left-0 w-[400px] h-[400px] bg-brand-primary/5 blur-[120px] rounded-full pointer-events-none"></div>
      <div className="absolute bottom-0 right-0 w-[400px] h-[400px] bg-brand-accent/5 blur-[120px] rounded-full pointer-events-none"></div>

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
          
          <h1 className="text-3xl font-black text-brand-heading text-center mb-2 tracking-tight">Join YourTripy</h1>
          <p className="text-brand-slate text-sm text-center mb-8 font-medium">The AI Co-pilot for modern explorers.</p>

          {error && (
            <div className="mb-6 p-4 bg-red-500/10 border border-red-500/20 rounded-2xl flex items-center gap-3 text-red-400 text-xs font-bold animate-in fade-in slide-in-from-top-2">
              <AlertCircle className="w-4 h-4 shrink-0" />
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-2">
              <label className="text-[10px] font-black uppercase tracking-widest text-brand-slate ml-1">Full Name</label>
              <div className="relative">
                <UserIcon className="absolute left-4 top-1/2 -translate-y-1/2 text-brand-slate w-4 h-4" />
                <input 
                  type="text" 
                  required 
                  placeholder="Captain Cook"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  className="w-full pl-12 pr-5 py-4 rounded-2xl bg-brand-deep border border-brand-primary/10 text-brand-heading focus:border-brand-primary outline-none transition-all placeholder:text-brand-slate/40 font-medium" 
                />
              </div>
            </div>

            <div className="space-y-2">
              <label className="text-[10px] font-black uppercase tracking-widest text-brand-slate ml-1">Email Address</label>
              <div className="relative">
                <Mail className="absolute left-4 top-1/2 -translate-y-1/2 text-brand-slate w-4 h-4" />
                <input 
                  type="email" 
                  required 
                  placeholder="explorer@yourtripy.com"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className="w-full pl-12 pr-5 py-4 rounded-2xl bg-brand-deep border border-brand-primary/10 text-brand-heading focus:border-brand-primary outline-none transition-all placeholder:text-brand-slate/40 font-medium" 
                />
              </div>
            </div>

            <div className="space-y-2">
              <label className="text-[10px] font-black uppercase tracking-widest text-brand-slate ml-1">Password</label>
              <div className="relative">
                <Lock className="absolute left-4 top-1/2 -translate-y-1/2 text-brand-slate w-4 h-4" />
                <input 
                  type="password" 
                  required 
                  placeholder="••••••••"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="w-full pl-12 pr-5 py-4 rounded-2xl bg-brand-deep border border-brand-primary/10 text-brand-heading focus:border-brand-primary outline-none transition-all placeholder:text-brand-slate/40 font-medium" 
                />
              </div>
            </div>

            <button 
              type="submit" 
              disabled={isLoading}
              className="w-full py-4 mt-4 rounded-2xl bg-brand-primary text-white font-black hover:bg-[#FF9A3C] transition-all flex items-center justify-center gap-2 shadow-xl shadow-brand-primary/20 active:scale-95 disabled:opacity-50"
            >
              {isLoading ? 'Creating account...' : 'Create Account'}
              <Send className="w-4 h-4" />
            </button>
          </form>



          <div className="mt-8 text-center text-xs text-brand-slate font-medium">
            Already have an account?{' '}
            <button 
              onClick={() => navigateTo('login')}
              className="text-brand-primary font-black hover:underline ml-1"
            >
              Sign In
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SignUpPage;
