import { motion } from 'motion/react';
import { useEffect, useState } from 'react';

interface CodeDrop {
  id: number;
  x: number;
  chars: string[];
  speed: number;
  delay: number;
}

export function CodeRain() {
  const [drops, setDrops] = useState<CodeDrop[]>([]);
  const codeChars = ['0', '1', '{', '}', '<', '>', '/', '*', '+', '=', ';', '(', ')', '[', ']', 'AI', 'ML', 'GPU'];

  useEffect(() => {
    const newDrops = Array.from({ length: 30 }, (_, i) => ({
      id: i,
      x: Math.random() * 100,
      chars: Array.from({ length: 8 }, () => codeChars[Math.floor(Math.random() * codeChars.length)]),
      speed: 3 + Math.random() * 2,
      delay: Math.random() * 3,
    }));
    setDrops(newDrops);
  }, []);

  return (
    <div className="absolute inset-0 overflow-hidden pointer-events-none">
      {drops.map((drop) => (
        <motion.div
          key={drop.id}
          className="absolute flex flex-col gap-1"
          style={{
            left: `${drop.x}%`,
            top: -100,
          }}
          animate={{
            y: ['0vh', '120vh'],
          }}
          transition={{
            duration: drop.speed,
            repeat: Infinity,
            delay: drop.delay,
            ease: 'linear',
          }}
        >
          {drop.chars.map((char, index) => (
            <motion.span
              key={index}
              className="text-emerald-400/40 text-sm font-mono"
              animate={{
                opacity: [0, 1, 0],
              }}
              transition={{
                duration: 1,
                delay: index * 0.1,
                repeat: Infinity,
              }}
            >
              {char}
            </motion.span>
          ))}
        </motion.div>
      ))}
    </div>
  );
}
