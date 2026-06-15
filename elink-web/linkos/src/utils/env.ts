const isEnv = (env) => {
  return import.meta.env.MODE === env;
};

const isDev = () => isEnv("development");

const isProd = () => isEnv("production");

export { isDev, isProd };
