import React, { useState, useEffect, lazy, Suspense } from 'react';
import { supabase } from './supabase';
import LandingPage from './LandingPage';
const HowItWorksPage = lazy(() => import('./pages/HowItWorksPage'));
const FeaturesPage = lazy(() => import('./pages/FeaturesPage'));
const BlogPage = lazy(() => import('./pages/BlogPage'));
const GuidebooksPage = lazy(() => import('./pages/GuidebooksPage'));
const CommunityPage = lazy(() => import('./pages/CommunityPage'));
const SupportPage = lazy(() => import('./pages/SupportPage'));
const AboutPage = lazy(() => import('./pages/AboutPage'));
const PrivacyPolicyPage = lazy(() => import('./pages/PrivacyPolicyPage'));
const TermsOfServicePage = lazy(() => import('./pages/TermsOfServicePage'));
const ContactPage = lazy(() => import('./pages/ContactPage'));
const PlannerPage = lazy(() => import('./pages/PlannerPage'));
const LoginPage = lazy(() => import('./pages/LoginPage'));
const SignUpPage = lazy(() => import('./pages/SignUpPage'));
const AdminPage = lazy(() => import('./pages/AdminPage'));
const ProfilePage = lazy(() => import('./pages/ProfilePage'));
const MyTripsPage = lazy(() => import('./pages/MyTripsPage'));

import Navbar from './components/Navbar';
import Footer from './components/Footer';
import ErrorBoundary from './components/ErrorBoundary';
import TopLoadingBar from './components/ui/TopLoadingBar';

export type ViewType = 'home' | 'how-it-works' | 'features' | 'blog' | 'guidebooks' | 'community' | 'support' | 'about' | 'privacy' | 'terms' | 'contact' | 'planner' | 'login' | 'signup' | 'admin' | 'profile' | 'trips';

export interface User {
  id?: string;
  name: string;
  email: string;
  avatar?: string;
}

const App: React.FC = () => {
  const [view, setView] = useState<ViewType>('home');
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedTripId, setSelectedTripId] = useState<string | null>(null);
  const [user, setUser] = useState<User | null>(null);

  useEffect(() => {
    // Standardize to Light theme
    document.documentElement.classList.remove('dark');
    
    // Handle initial path and popstate (browser back/forward)
    const handlePathChange = () => {
      const pathStr = window.location.pathname.replace(/^\/+/, '');
      const validViews: ViewType[] = ['home', 'how-it-works', 'features', 'blog', 'guidebooks', 'community', 'support', 'about', 'privacy', 'terms', 'contact', 'planner', 'login', 'signup', 'admin', 'profile', 'trips'];
      
      if (!pathStr || pathStr === '') {
        setView('home');
      } else if (validViews.includes(pathStr as ViewType)) {
        setView(pathStr as ViewType);
      } else {
        setView('home');
      }
    };

    window.addEventListener('popstate', handlePathChange);
    handlePathChange(); // Check on mount

    // Supabase Auth session check on mount
    supabase.auth.getSession().then(({ data: { session } }) => {
      if (session?.user) {
        const u = session.user;
        setUser({
          id: u.id,
          name: u.user_metadata?.name || u.user_metadata?.full_name || u.email?.split('@')[0] || 'Explorer',
          email: u.email || '',
          avatar: u.user_metadata?.avatar_url || `https://ui-avatars.com/api/?name=${u.email}&background=FACC15&color=020617`
        });
      } else {
        setUser(null);
      }
    });

    // Supabase Auth real-time state change listener
    const { data: { subscription } } = supabase.auth.onAuthStateChange((_event, session) => {
      if (session?.user) {
        const u = session.user;
        setUser({
          id: u.id,
          name: u.user_metadata?.name || u.user_metadata?.full_name || u.email?.split('@')[0] || 'Explorer',
          email: u.email || '',
          avatar: u.user_metadata?.avatar_url || `https://ui-avatars.com/api/?name=${u.email}&background=FACC15&color=020617`
        });
      } else {
        setUser(null);
      }
    });

    return () => {
      subscription.unsubscribe();
      window.removeEventListener('popstate', handlePathChange);
    };
  }, []);

  const updateUser = (updates: Partial<User>) => {
    setUser((prev) => (prev ? { ...prev, ...updates } : null));
  };

  const navigateTo = (newView: ViewType, query: string = '', tripId: string | null = null) => {
    setSearchQuery(query);
    setSelectedTripId(tripId);
    setView(newView);
    const newPath = newView === 'home' ? '/' : `/${newView}`;
    if (window.location.pathname !== newPath) {
      window.history.pushState({}, '', newPath);
    }
    window.scrollTo(0, 0);
  };

  const handleLogin = (userData: User) => {
    setUser(userData);
    navigateTo('home');
  };

  const handleLogout = async () => {
    try {
      await supabase.auth.signOut();
      setUser(null);
      navigateTo('home');
    } catch (err) {
      console.error("Logout error:", err);
    }
  };

  const renderView = () => {
    switch (view) {
      case 'planner': return <PlannerPage initialQuery={searchQuery} tripId={selectedTripId} navigateTo={navigateTo} />;
      case 'how-it-works': return <HowItWorksPage />;
      case 'features': return <FeaturesPage />;
      case 'blog': return <BlogPage />;
      case 'guidebooks': return <GuidebooksPage />;
      case 'community': return <CommunityPage />;
      case 'support': return <SupportPage />;
      case 'about': return <AboutPage />;
      case 'privacy': return <PrivacyPolicyPage />;
      case 'terms': return <TermsOfServicePage />;
      case 'contact': return <ContactPage navigateTo={navigateTo} />;
      case 'login': return <LoginPage navigateTo={navigateTo} onLogin={handleLogin} />;
      case 'signup': return <SignUpPage navigateTo={navigateTo} onLogin={handleLogin} />;
      case 'admin': return <AdminPage navigateTo={navigateTo} user={user} />;
      case 'profile': return <ProfilePage navigateTo={navigateTo} user={user} updateUser={updateUser} />;
      case 'trips': return <MyTripsPage navigateTo={navigateTo} user={user} />;
      default: return <LandingPage onExplore={() => navigateTo('planner')} navigateTo={navigateTo} user={user} />;
    }
  };

  return (
    <ErrorBoundary>
      <div className="min-h-screen bg-brand-deep text-brand-slate font-sans selection:bg-brand-primary selection:text-white flex flex-col justify-between">
        <TopLoadingBar />
        <Navbar 
          currentView={view} 
          navigateTo={navigateTo} 
          user={user} 
          onLogout={handleLogout} 
        />
        <main className="flex-grow">
          <Suspense fallback={
            <div className="min-h-screen flex items-center justify-center bg-brand-deep">
              <div className="w-12 h-12 border-4 border-brand-primary/20 border-t-brand-primary rounded-full animate-spin"></div>
            </div>
          }>
            {renderView()}
          </Suspense>
        </main>
        <Footer navigateTo={navigateTo} />
      </div>
    </ErrorBoundary>
  );
};

export default App;
