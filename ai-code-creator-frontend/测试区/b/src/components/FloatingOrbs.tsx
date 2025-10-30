import { motion } from 'motion/react';

export function FloatingOrbs() {
  const orbs = [
    { size: 300, color: 'from-purple-500/30 to-pink-500/30', duration: 20, delay: 0 },
    { size: 250, color: 'from-blue-500/30 to-cyan-500/30', duration: 18, delay: 2 },
    { size: 200, color: 'from-indigo-500/30 to-purple-500/30', duration: 22, delay: 4 },
  ];

  return (
    <div className="absolute inset-0 overflow-hidden pointer-events-none">
      {orbs.map((orb, index) => (
        <motion.div
          key={index}
          className={`absolute rounded-full bg-gradient-to-br ${orb.color} blur-3xl`}
          style={{
            width: orb.size,
            height: orb.size,
            left: `${Math.random() * 100}%`,
            top: `${Math.random() * 100}%`,
          }}
          animate={{
            x: [0, 100, -100, 0],
            y: [0, -100, 100, 0],
            scale: [1, 1.2, 0.8, 1],
          }}
          transition={{
            duration: orb.duration,
            repeat: Infinity,
            delay: orb.delay,
            ease: 'easeInOut',
          }}
        />
      ))}
    </div>
  );
}
