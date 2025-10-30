import { motion } from 'motion/react';

export function EnergyWaves() {
  return (
    <div className="absolute inset-0 overflow-hidden pointer-events-none">
      {[...Array(5)].map((_, i) => (
        <motion.div
          key={i}
          className="absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2"
          initial={{ scale: 0, opacity: 0.6 }}
          animate={{
            scale: [0, 3],
            opacity: [0.6, 0],
          }}
          transition={{
            duration: 4,
            repeat: Infinity,
            delay: i * 0.8,
            ease: 'easeOut',
          }}
        >
          <div className="w-96 h-96 rounded-full border-2 border-cyan-400/50" />
        </motion.div>
      ))}
    </div>
  );
}
