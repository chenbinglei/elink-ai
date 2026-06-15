/**
 * 环境判断工具
 */
const isEnv = (env) => {
  return process.env.NODE_ENV === env;
};

const isDev = () => isEnv("development");

const isProd = () => isEnv("production");

export { isDev, isProd };
