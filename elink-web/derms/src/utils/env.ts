const isEnv = (env: string): boolean => {
  return import.meta.env.MODE === env;
};

const isDev = (): boolean => isEnv("development");

const isProd = (): boolean => isEnv("production");

export { isDev, isProd };
