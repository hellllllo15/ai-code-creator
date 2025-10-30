import { motion } from 'motion/react';
import { useEffect, useState } from 'react';

interface Rune {
  id: number;
  char: string;
  x: number;
  y: number;
  duration: number;
  delay: number;
}

export function FloatingRunes() {
  const [runes, setRunes] = useState<Rune[]>([]);
  const runeChars = ['剑', '道', '仙', '灵', '气', '元', '神', '修', '玄', '武', '法', '诀', '阵', '符', '丹'];

  useEffect(() => {
    const newRunes = Array.from({ length: 20 }, (_, i) => ({
      id: i,
      char: runeChars[Math.floor(Math.random() * runeChars.length)],
      x: Math.random() * 100,
      y: Math.random() * 100,
      duration: 8 + Math.random() * 4,
      delay: Math.random() * 3,
    }));
    setRunes(newRunes);
  }, []);

  return (
    <div className="absolute inset-0 overflow-hidden pointer-events-none">
      {runes.map((rune) => (
        <motion.div
          key={rune.id}
          className="absolute text-2xl text-cyan-400/20"
          style={{
            left: `${rune.x}%`,
            top: `${rune.y}%`,
            textShadow: '0 0 10px rgba(34, 211, 238, 0.3)',
          }}
          animate={{
            y: [0, -30, 0],
            opacity: [0.1, 0.3, 0.1],
            rotate: [0, 360],
          }}
          transition={{
            duration: rune.duration,
            repeat: Infinity,
            delay: rune.delay,
            ease: 'easeInOut',
          }}
        >
          {rune.char}
        </motion.div>
      ))}
    </div>
  );
}
