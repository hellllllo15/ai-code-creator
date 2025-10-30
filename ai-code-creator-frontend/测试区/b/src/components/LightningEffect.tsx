import { motion } from 'motion/react';
import { useEffect, useState } from 'react';

export function LightningEffect() {
  const [show, setShow] = useState(false);

  useEffect(() => {
    const interval = setInterval(() => {
      setShow(true);
      setTimeout(() => setShow(false), 200);
    }, 5000 + Math.random() * 5000);

    return () => clearInterval(interval);
  }, []);

  if (!show) return null;

  return (
    <motion.div
      className="absolute inset-0 pointer-events-none"
      initial={{ opacity: 0 }}
      animate={{ opacity: [0, 0.3, 0] }}
      transition={{ duration: 0.2 }}
    >
      <svg className="w-full h-full" xmlns="http://www.w3.org/2000/svg">
        <path
          d={`M ${Math.random() * 100}% 0 L ${Math.random() * 100}% 30% L ${Math.random() * 100}% 50% L ${Math.random() * 100}% 100%`}
          stroke="url(#lightning-gradient)"
          strokeWidth="3"
          fill="none"
          filter="url(#glow)"
        />
        <defs>
          <linearGradient id="lightning-gradient" x1="0%" y1="0%" x2="0%" y2="100%">
            <stop offset="0%" stopColor="#06b6d4" stopOpacity="1" />
            <stop offset="100%" stopColor="#3b82f6" stopOpacity="1" />
          </linearGradient>
          <filter id="glow">
            <feGaussianBlur stdDeviation="4" result="coloredBlur" />
            <feMerge>
              <feMergeNode in="coloredBlur" />
              <feMergeNode in="SourceGraphic" />
            </feMerge>
          </filter>
        </defs>
      </svg>
    </motion.div>
  );
}
