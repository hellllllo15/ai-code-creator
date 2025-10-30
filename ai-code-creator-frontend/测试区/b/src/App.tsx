import { useState } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { Code2, Sparkles, Cpu, Zap, Mail, Lock, User, Github, Chrome, Database, Terminal, Braces, Box, FileCode } from 'lucide-react';
import { Input } from './components/ui/input';
import { Label } from './components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './components/ui/select';
import { CodeRain } from './components/CodeRain';
import { AIParticles } from './components/AIParticles';
import { NeuralNetwork } from './components/NeuralNetwork';
import { DataStream } from './components/DataStream';

const models = [
  { name: 'GPT-4', desc: '最强大的语言模型', color: 'from-emerald-400 to-emerald-600' },
  { name: 'Claude 3', desc: '高级推理能力', color: 'from-blue-400 to-blue-600' },
  { name: 'Gemini Pro', desc: '多模态AI模型', color: 'from-purple-400 to-purple-600' },
  { name: 'LLaMA 3', desc: '开源大语言模型', color: 'from-orange-400 to-orange-600' },
];

const frameworks = [
  { name: 'React', icon: Code2, color: 'text-cyan-400' },
  { name: 'Vue', icon: Box, color: 'text-emerald-400' },
  { name: 'Angular', icon: Braces, color: 'text-red-400' },
  { name: 'Svelte', icon: FileCode, color: 'text-orange-400' },
  { name: 'Next.js', icon: Terminal, color: 'text-white' },
];

export default function App() {
  const [mode, setMode] = useState<'login' | 'register'>('login');
  const [selectedFramework, setSelectedFramework] = useState(0);

  return (
    <div className="min-h-screen relative bg-black overflow-hidden">
      {/* 复杂背景层 */}
      <div className="absolute inset-0 bg-gradient-to-br from-slate-950 via-emerald-950/30 to-slate-950" />
      <div className="absolute inset-0 bg-[radial-gradient(ellipse_at_center,_var(--tw-gradient-stops))] from-emerald-900/20 via-transparent to-transparent" />
      
      {/* 网格背景 */}
      <div className="absolute inset-0 opacity-20">
        <div className="absolute inset-0 bg-[linear-gradient(to_right,#10b98120_1px,transparent_1px),linear-gradient(to_bottom,#10b98120_1px,transparent_1px)] bg-[size:4rem_4rem]" />
      </div>

      {/* 动态效果层 */}
      <CodeRain />
      <AIParticles />
      <NeuralNetwork />
      <DataStream />

      {/* 主内容区域 */}
      <div className="relative z-10 flex min-h-screen">
        {/* 左侧信息面板 */}
        <motion.div
          initial={{ x: -100, opacity: 0 }}
          animate={{ x: 0, opacity: 1 }}
          transition={{ duration: 1 }}
          className="hidden lg:flex lg:w-2/5 relative"
        >
          <div className="flex-1 p-12 flex flex-col justify-between">
            {/* 顶部 Logo 和标题 */}
            <div>
              <motion.div
                className="inline-block mb-6"
                animate={{ 
                  boxShadow: [
                    '0 0 20px rgba(16, 185, 129, 0.3)',
                    '0 0 40px rgba(16, 185, 129, 0.5)',
                    '0 0 20px rgba(16, 185, 129, 0.3)',
                  ]
                }}
                transition={{
                  duration: 2,
                  repeat: Infinity,
                  ease: 'easeInOut',
                }}
              >
                <div className="relative">
                  <div className="absolute inset-0 bg-gradient-to-r from-emerald-400 to-blue-600 rounded-2xl blur-xl opacity-50" />
                  <div className="relative w-16 h-16 bg-gradient-to-br from-emerald-500 to-blue-600 rounded-2xl flex items-center justify-center border-2 border-emerald-300/50">
                    <Code2 className="w-8 h-8 text-white" />
                  </div>
                </div>
              </motion.div>
              
              <h1 className="text-5xl mb-4 bg-gradient-to-r from-emerald-400 via-blue-400 to-purple-400 text-transparent bg-clip-text">
                AI Code Studio
              </h1>
              <p className="text-xl text-emerald-200/70 mb-8">
                Transform ideas into code<br />
                Build faster with AI
              </p>

              {/* AI 模型展示 */}
              <div className="space-y-3 mb-8">
                <h3 className="text-sm text-emerald-300/50 uppercase tracking-wider flex items-center gap-2">
                  <Cpu className="w-4 h-4" />
                  支持的AI模型
                </h3>
                {models.map((model, index) => (
                  <motion.div
                    key={model.name}
                    initial={{ x: -50, opacity: 0 }}
                    animate={{ x: 0, opacity: 1 }}
                    transition={{ delay: index * 0.1 + 0.5 }}
                    className="group relative"
                  >
                    <div className="flex items-center gap-3 p-3 rounded-lg bg-slate-900/30 border border-emerald-500/20 hover:border-emerald-500/40 transition-all">
                      <div className={`w-2 h-2 rounded-full bg-gradient-to-r ${model.color}`} />
                      <div className="flex-1">
                        <div className="text-emerald-100/90">{model.name}</div>
                        <div className="text-xs text-emerald-300/50">{model.desc}</div>
                      </div>
                      <Sparkles className="w-4 h-4 text-emerald-400/50 group-hover:text-emerald-400 transition-colors" />
                    </div>
                  </motion.div>
                ))}
              </div>

              {/* 特性列表 */}
              <div className="space-y-2">
                {[
                  { icon: Zap, text: '实时代码生成' },
                  { icon: Terminal, text: '智能补全建议' },
                  { icon: Database, text: '项目模板库' },
                ].map((feature, index) => {
                  const Icon = feature.icon;
                  return (
                    <motion.div
                      key={feature.text}
                      initial={{ x: -30, opacity: 0 }}
                      animate={{ x: 0, opacity: 1 }}
                      transition={{ delay: 1 + index * 0.1 }}
                      className="flex items-center gap-3 text-emerald-300/70"
                    >
                      <Icon className="w-4 h-4" />
                      <span className="text-sm">{feature.text}</span>
                    </motion.div>
                  );
                })}
              </div>
            </div>

            {/* 底部统计 */}
            <div className="grid grid-cols-3 gap-4">
              {[
                { label: '活跃开发者', value: '50K+' },
                { label: '生成项目', value: '1M+' },
                { label: '代码行数', value: '100M+' },
              ].map((stat, index) => (
                <motion.div
                  key={stat.label}
                  initial={{ y: 20, opacity: 0 }}
                  animate={{ y: 0, opacity: 1 }}
                  transition={{ delay: 1.2 + index * 0.1 }}
                  className="relative group"
                >
                  <div className="absolute inset-0 bg-gradient-to-t from-emerald-500/10 to-transparent rounded-lg blur-sm group-hover:from-emerald-500/20 transition-all" />
                  <div className="relative p-3 border border-emerald-500/20 rounded-lg backdrop-blur-sm hover:border-emerald-500/40 transition-all">
                    <div className="text-2xl text-emerald-400 mb-1">{stat.value}</div>
                    <div className="text-xs text-emerald-300/50">{stat.label}</div>
                  </div>
                </motion.div>
              ))}
            </div>
          </div>
        </motion.div>

        {/* 右侧表单区域 */}
        <div className="flex-1 flex items-center justify-center p-8">
          <motion.div
            initial={{ opacity: 0, scale: 0.9 }}
            animate={{ opacity: 1, scale: 1 }}
            transition={{ duration: 0.8, delay: 0.3 }}
            className="w-full max-w-2xl"
          >
            {/* 主卡片 */}
            <div className="relative">
              {/* 外层光环 */}
              <motion.div
                className="absolute -inset-1 bg-gradient-to-r from-emerald-600 via-blue-600 to-purple-600 rounded-3xl blur-xl opacity-30"
                animate={{
                  opacity: [0.3, 0.5, 0.3],
                }}
                transition={{
                  duration: 3,
                  repeat: Infinity,
                  ease: 'easeInOut',
                }}
              />

              {/* 角装饰 - 代码风格 */}
              <div className="absolute -top-3 -left-3 text-emerald-400/30 font-mono text-xs"><div></div>
              <div className="absolute -top-3 -right-3 text-emerald-400/30 font-mono text-xs"></div></div>
              <div className="absolute -bottom-3 -left-3 text-emerald-400/30 font-mono text-xs">{'{ }'}</div>
              <div className="absolute -bottom-3 -right-3 text-emerald-400/30 font-mono text-xs">[ ]</div>

              {/* 主体内容 */}
              <div className="relative bg-slate-950/90 backdrop-blur-2xl border border-emerald-500/30 rounded-2xl p-10 shadow-2xl">
                {/* 顶部装饰 */}
                <div className="flex items-center justify-center mb-8">
                  <div className="flex items-center gap-4">
                    <motion.div
                      className="h-px w-12 bg-gradient-to-r from-transparent to-emerald-500"
                      animate={{
                        width: ['3rem', '4rem', '3rem'],
                      }}
                      transition={{
                        duration: 2,
                        repeat: Infinity,
                        ease: 'easeInOut',
                      }}
                    />
                    <Terminal className="w-8 h-8 text-emerald-400" />
                    <motion.div
                      className="h-px w-12 bg-gradient-to-l from-transparent to-emerald-500"
                      animate={{
                        width: ['3rem', '4rem', '3rem'],
                      }}
                      transition={{
                        duration: 2,
                        repeat: Infinity,
                        ease: 'easeInOut',
                        delay: 1,
                      }}
                    />
                  </div>
                </div>

                {/* 切换按钮 */}
                <div className="flex gap-3 mb-8">
                  <button
                    onClick={() => setMode('login')}
                    className={`flex-1 relative py-4 px-6 rounded-xl transition-all duration-500 overflow-hidden group ${
                      mode === 'login' ? 'text-white' : 'text-emerald-300/50'
                    }`}
                  >
                    {mode === 'login' && (
                      <motion.div
                        layoutId="activeTab"
                        className="absolute inset-0 bg-gradient-to-r from-emerald-600 to-blue-600"
                        transition={{ type: 'spring', bounce: 0.2, duration: 0.6 }}
                      />
                    )}
                    <span className="relative flex items-center justify-center gap-2 z-10">
                      <Terminal className="w-5 h-5" />
                      <span>登录</span>
                    </span>
                    {mode !== 'login' && (
                      <div className="absolute inset-0 bg-slate-800/30 group-hover:bg-slate-800/50 transition-colors" />
                    )}
                  </button>

                  <button
                    onClick={() => setMode('register')}
                    className={`flex-1 relative py-4 px-6 rounded-xl transition-all duration-500 overflow-hidden group ${
                      mode === 'register' ? 'text-white' : 'text-emerald-300/50'
                    }`}
                  >
                    {mode === 'register' && (
                      <motion.div
                        layoutId="activeTab"
                        className="absolute inset-0 bg-gradient-to-r from-emerald-600 to-blue-600"
                        transition={{ type: 'spring', bounce: 0.2, duration: 0.6 }}
                      />
                    )}
                    <span className="relative flex items-center justify-center gap-2 z-10">
                      <Code2 className="w-5 h-5" />
                      <span>注册</span>
                    </span>
                    {mode !== 'register' && (
                      <div className="absolute inset-0 bg-slate-800/30 group-hover:bg-slate-800/50 transition-colors" />
                    )}
                  </button>
                </div>

                {/* 表单内容 */}
                <AnimatePresence mode="wait">
                  {mode === 'login' ? (
                    <motion.div
                      key="login"
                      initial={{ opacity: 0, x: -20 }}
                      animate={{ opacity: 1, x: 0 }}
                      exit={{ opacity: 0, x: 20 }}
                      transition={{ duration: 0.4 }}
                    >
                      <form className="space-y-6">
                        <div className="space-y-2">
                          <Label className="text-emerald-200 flex items-center gap-2">
                            <Mail className="w-4 h-4" />
                            Email
                          </Label>
                          <Input
                            type="email"
                            placeholder="developer@ai-studio.dev"
                            className="bg-slate-900/50 border-emerald-500/30 text-white placeholder:text-emerald-300/30 focus:border-emerald-400 focus:ring-emerald-400/20 h-12 font-mono"
                          />
                        </div>

                        <div className="space-y-2">
                          <Label className="text-emerald-200 flex items-center gap-2">
                            <Lock className="w-4 h-4" />
                            Password
                          </Label>
                          <Input
                            type="password"
                            placeholder="••••••••"
                            className="bg-slate-900/50 border-emerald-500/30 text-white placeholder:text-emerald-300/30 focus:border-emerald-400 focus:ring-emerald-400/20 h-12 font-mono"
                          />
                        </div>

                        <div className="flex items-center justify-between text-sm">
                          <label className="flex items-center gap-2 text-emerald-300/70 cursor-pointer">
                            <input type="checkbox" className="rounded border-emerald-500/30 bg-slate-900/50" />
                            记住登录
                          </label>
                          <button type="button" className="text-emerald-400 hover:text-emerald-300 transition-colors">
                            忘记密码？
                          </button>
                        </div>

                        <button
                          type="submit"
                          className="w-full relative group overflow-hidden rounded-xl py-4"
                        >
                          <div className="absolute inset-0 bg-gradient-to-r from-emerald-600 via-blue-600 to-purple-600 bg-[length:200%_100%] animate-[shimmer_3s_linear_infinite]" />
                          <div className="absolute inset-0 bg-gradient-to-r from-emerald-600 to-blue-600 opacity-0 group-hover:opacity-100 transition-opacity" />
                          <span className="relative flex items-center justify-center gap-2 text-white">
                            <Zap className="w-5 h-5" />
                            开始编码
                          </span>
                        </button>

                        {/* 社交登录 */}
                        <div className="relative">
                          <div className="absolute inset-0 flex items-center">
                            <div className="w-full border-t border-emerald-500/20" />
                          </div>
                          <div className="relative flex justify-center text-xs uppercase">
                            <span className="bg-slate-950 px-2 text-emerald-300/50">或使用</span>
                          </div>
                        </div>

                        <div className="grid grid-cols-2 gap-3">
                          <button
                            type="button"
                            className="flex items-center justify-center gap-2 py-3 px-4 bg-slate-900/50 border border-emerald-500/30 rounded-lg text-emerald-300 hover:border-emerald-500/50 hover:bg-slate-900/70 transition-all"
                          >
                            <Github className="w-5 h-5" />
                            GitHub
                          </button>
                          <button
                            type="button"
                            className="flex items-center justify-center gap-2 py-3 px-4 bg-slate-900/50 border border-emerald-500/30 rounded-lg text-emerald-300 hover:border-emerald-500/50 hover:bg-slate-900/70 transition-all"
                          >
                            <Chrome className="w-5 h-5" />
                            Google
                          </button>
                        </div>
                      </form>
                    </motion.div>
                  ) : (
                    <motion.div
                      key="register"
                      initial={{ opacity: 0, x: -20 }}
                      animate={{ opacity: 1, x: 0 }}
                      exit={{ opacity: 0, x: 20 }}
                      transition={{ duration: 0.4 }}
                    >
                      <form className="space-y-5">
                        <div className="grid grid-cols-2 gap-4">
                          <div className="space-y-2">
                            <Label className="text-emerald-200 flex items-center gap-2">
                              <User className="w-4 h-4" />
                              用户名
                            </Label>
                            <Input
                              type="text"
                              placeholder="developer"
                              className="bg-slate-900/50 border-emerald-500/30 text-white placeholder:text-emerald-300/30 focus:border-emerald-400 focus:ring-emerald-400/20 font-mono"
                            />
                          </div>

                          <div className="space-y-2">
                            <Label className="text-emerald-200 flex items-center gap-2">
                              <Cpu className="w-4 h-4" />
                              AI模型
                            </Label>
                            <Select defaultValue="gpt4">
                              <SelectTrigger className="bg-slate-900/50 border-emerald-500/30 text-white focus:border-emerald-400 focus:ring-emerald-400/20">
                                <SelectValue placeholder="选择模型" />
                              </SelectTrigger>
                              <SelectContent className="bg-slate-900 border-emerald-500/30">
                                <SelectItem value="gpt4">GPT-4</SelectItem>
                                <SelectItem value="claude">Claude 3</SelectItem>
                                <SelectItem value="gemini">Gemini Pro</SelectItem>
                                <SelectItem value="llama">LLaMA 3</SelectItem>
                              </SelectContent>
                            </Select>
                          </div>
                        </div>

                        <div className="space-y-2">
                          <Label className="text-emerald-200 flex items-center gap-2">
                            <Code2 className="w-4 h-4" />
                            首选框架
                          </Label>
                          <div className="grid grid-cols-5 gap-2">
                            {frameworks.map((framework, index) => {
                              const Icon = framework.icon;
                              return (
                                <button
                                  key={framework.name}
                                  type="button"
                                  onClick={() => setSelectedFramework(index)}
                                  className={`relative p-3 rounded-lg border transition-all ${
                                    selectedFramework === index
                                      ? 'border-emerald-400 bg-emerald-500/20'
                                      : 'border-emerald-500/30 bg-slate-900/50 hover:border-emerald-500/50'
                                  }`}
                                >
                                  <Icon className={`w-6 h-6 mx-auto ${framework.color}`} />
                                  <span className="text-xs text-emerald-300/70 block mt-1">{framework.name}</span>
                                  {selectedFramework === index && (
                                    <motion.div
                                      layoutId="selectedFramework"
                                      className="absolute inset-0 border-2 border-emerald-400 rounded-lg"
                                      transition={{ type: 'spring', bounce: 0.2, duration: 0.6 }}
                                    />
                                  )}
                                </button>
                              );
                            })}
                          </div>
                        </div>

                        <div className="space-y-2">
                          <Label className="text-emerald-200 flex items-center gap-2">
                            <Mail className="w-4 h-4" />
                            Email
                          </Label>
                          <Input
                            type="email"
                            placeholder="developer@ai-studio.dev"
                            className="bg-slate-900/50 border-emerald-500/30 text-white placeholder:text-emerald-300/30 focus:border-emerald-400 focus:ring-emerald-400/20 font-mono"
                          />
                        </div>

                        <div className="grid grid-cols-2 gap-4">
                          <div className="space-y-2">
                            <Label className="text-emerald-200 flex items-center gap-2">
                              <Lock className="w-4 h-4" />
                              密码
                            </Label>
                            <Input
                              type="password"
                              placeholder="••••••••"
                              className="bg-slate-900/50 border-emerald-500/30 text-white placeholder:text-emerald-300/30 focus:border-emerald-400 focus:ring-emerald-400/20 font-mono"
                            />
                          </div>

                          <div className="space-y-2">
                            <Label className="text-emerald-200 flex items-center gap-2">
                              <Lock className="w-4 h-4" />
                              确认密码
                            </Label>
                            <Input
                              type="password"
                              placeholder="••••••••"
                              className="bg-slate-900/50 border-emerald-500/30 text-white placeholder:text-emerald-300/30 focus:border-emerald-400 focus:ring-emerald-400/20 font-mono"
                            />
                          </div>
                        </div>

                        <label className="flex items-start gap-2 text-sm text-emerald-300/70 cursor-pointer">
                          <input type="checkbox" className="rounded border-emerald-500/30 bg-slate-900/50 mt-0.5" />
                          <span>
                            我同意
                            <button type="button" className="text-emerald-400 hover:text-emerald-300 mx-1">服务条款</button>
                            和
                            <button type="button" className="text-emerald-400 hover:text-emerald-300 mx-1">隐私政策</button>
                          </span>
                        </label>

                        <button
                          type="submit"
                          className="w-full relative group overflow-hidden rounded-xl py-4"
                        >
                          <div className="absolute inset-0 bg-gradient-to-r from-emerald-600 via-blue-600 to-purple-600 bg-[length:200%_100%] animate-[shimmer_3s_linear_infinite]" />
                          <div className="absolute inset-0 bg-gradient-to-r from-emerald-600 to-blue-600 opacity-0 group-hover:opacity-100 transition-opacity" />
                          <span className="relative flex items-center justify-center gap-2 text-white">
                            <Sparkles className="w-5 h-5" />
                            开启AI编码之旅
                          </span>
                        </button>
                      </form>
                    </motion.div>
                  )}
                </AnimatePresence>

                {/* 底部切换 */}
                <div className="mt-8 pt-6 border-t border-emerald-500/20">
                  <p className="text-center text-emerald-300/50 text-sm">
                    {mode === 'login' ? '还没有账号？' : '已有账号？'}
                    <button
                      type="button"
                      onClick={() => setMode(mode === 'login' ? 'register' : 'login')}
                      className="text-emerald-400 hover:text-emerald-300 ml-2 transition-colors"
                    >
                      {mode === 'login' ? '立即注册' : '直接登录'}
                    </button>
                  </p>
                </div>

                {/* 装饰性代码片段 */}
                <div className="absolute top-4 right-4 text-emerald-400/20 font-mono text-xs">
                  const ai = true;
                </div>
                <div className="absolute bottom-4 left-4 text-emerald-400/20 font-mono text-xs">
                  {'// AI powered'}
                </div>
              </div>
            </div>

            {/* 底部提示 */}
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 1.2 }}
              className="mt-8 text-center space-y-2"
            >
              <p className="text-emerald-300/40 text-sm font-mono">
                {'< Build the future with AI />'}
              </p>
              <div className="flex items-center justify-center gap-4 text-emerald-400/30 text-xs">
                <span>v2.0.0</span>
                <span>•</span>
                <span>API Status: Online</span>
                <span>•</span>
                <span>99.9% Uptime</span>
              </div>
            </motion.div>
          </motion.div>
        </div>
      </div>

      {/* 装饰性扫描线 */}
      <motion.div
        className="absolute left-0 right-0 h-px bg-gradient-to-r from-transparent via-emerald-400/50 to-transparent"
        animate={{
          top: ['0%', '100%'],
        }}
        transition={{
          duration: 8,
          repeat: Infinity,
          ease: 'linear',
        }}
      />

      {/* 底部装饰光柱 */}
      {[...Array(8)].map((_, i) => (
        <motion.div
          key={i}
          className="absolute bottom-0 w-px bg-gradient-to-t from-emerald-400/30 via-emerald-400/10 to-transparent"
          style={{
            left: `${12.5 * (i + 1)}%`,
            height: '30%',
          }}
          animate={{
            opacity: [0.2, 0.6, 0.2],
            height: ['20%', '40%', '20%'],
          }}
          transition={{
            duration: 3 + i * 0.3,
            repeat: Infinity,
            ease: 'easeInOut',
            delay: i * 0.2,
          }}
        />
      ))}
    </div>
  );
}
