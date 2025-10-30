import { motion } from 'motion/react';
import { useEffect, useState } from 'react';

export function SwordAura() {
  const [swords, setSwords] = useState<Array<{ id: number; delay: number }>>([]);

  useEffect(() => {
    setSwords(Array.from({ length: 8 }, (_, i) => ({ id: i, delay: i * 0.3 })));
  }, []);

  return (
    <div className="absolute inset-0 overflow-hidden pointer-events-none">
      {swords.map((sword) => (
        <motion.div
          key={sword.id}
          className="absolute w-1 h-32 bg-gradient-to-b from-transparent via-cyan-400 to-transparent"
          style={{
            left: `${Math.random() * 100}%`,
            filter: 'blur(1px)',
          }}
          initial={{ y: -200, opacity: 0 }}
          animate={{
            y: ['0vh', '120vh'],
            opacity: [0, 1, 1, 0],
            x: [0, Math.random() * 50 - 25],
          }}
          transition={{
            duration: 3 + Math.random() * 2,
            repeat: Infinity,
            delay: sword.delay,
            ease: 'linear',
          }}
        />
      ))}
    </div>
  );
}
