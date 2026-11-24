import React, { useEffect, useState } from 'react';

const TopLoadingBar: React.FC = () => {
  const [progress, setProgress] = useState(0);
  const [visible, setVisible] = useState(true);

  useEffect(() => {
    // Quickly animate progress bar on initial page load
    const t1 = setTimeout(() => setProgress(35), 50);
    const t2 = setTimeout(() => setProgress(75), 180);
    const t3 = setTimeout(() => setProgress(100), 380);
    const t4 = setTimeout(() => setVisible(false), 650);

    return () => {
      clearTimeout(t1);
      clearTimeout(t2);
      clearTimeout(t3);
      clearTimeout(t4);
    };
  }, []);

  if (!visible) return null;

  return (
    <div className={`fixed top-0 left-0 w-full z-[101] pointer-events-none transition-opacity duration-300 ${progress >= 100 ? 'opacity-0' : 'opacity-100'}`}>
      <div 
        className="h-1 bg-brand-primary shadow-[0_0_15px_#FACC15] transition-all duration-300 ease-out rounded-r-full"
        style={{ width: `${progress}%` }}
      />
    </div>
  );
};

export default TopLoadingBar;
