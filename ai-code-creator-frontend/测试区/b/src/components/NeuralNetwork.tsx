import { motion } from 'motion/react';

export function NeuralNetwork() {
  const nodes = [
    { x: 20, y: 20 },
    { x: 20, y: 50 },
    { x: 20, y: 80 },
    { x: 50, y: 30 },
    { x: 50, y: 50 },
    { x: 50, y: 70 },
    { x: 80, y: 40 },
    { x: 80, y: 60 },
  ];

  const connections = [
    [0, 3], [0, 4], [0, 5],
    [1, 3], [1, 4], [1, 5],
    [2, 3], [2, 4], [2, 5],
    [3, 6], [3, 7],
    [4, 6], [4, 7],
    [5, 6], [5, 7],
  ];

  return (
    <div className="absolute inset-0 overflow-hidden pointer-events-none opacity-20">
      <svg className="w-full h-full" xmlns="http://www.w3.org/2000/svg">
        {connections.map((conn, index) => (
          <motion.line
            key={index}
            x1={`${nodes[conn[0]].x}%`}
            y1={`${nodes[conn[0]].y}%`}
            x2={`${nodes[conn[1]].x}%`}
            y2={`${nodes[conn[1]].y}%`}
            stroke="url(#line-gradient)"
            strokeWidth="1"
            initial={{ pathLength: 0, opacity: 0 }}
            animate={{ pathLength: 1, opacity: 0.4 }}
            transition={{
              duration: 2,
              delay: index * 0.1,
              repeat: Infinity,
              repeatDelay: 1,
            }}
          />
        ))}
        {nodes.map((node, index) => (
          <motion.circle
            key={index}
            cx={`${node.x}%`}
            cy={`${node.y}%`}
            r="4"
            fill="#10b981"
            initial={{ scale: 0 }}
            animate={{ scale: [0, 1.2, 1] }}
            transition={{
              duration: 0.5,
              delay: index * 0.1,
              repeat: Infinity,
              repeatDelay: 2,
            }}
          />
        ))}
        <defs>
          <linearGradient id="line-gradient" x1="0%" y1="0%" x2="100%" y2="0%">
            <stop offset="0%" stopColor="#10b981" stopOpacity="0.2" />
            <stop offset="50%" stopColor="#3b82f6" stopOpacity="0.6" />
            <stop offset="100%" stopColor="#10b981" stopOpacity="0.2" />
          </linearGradient>
        </defs>
      </svg>
    </div>
  );
}
