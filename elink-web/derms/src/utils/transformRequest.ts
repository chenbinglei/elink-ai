import qs from "qs";

export function transform(): (data: Record<string, unknown>) => string {
  return function (data: Record<string, unknown>): string {
    if (data instanceof FormData) {
      return data as unknown as string;
    }
    Object.keys(data).forEach((key) => {
      if (data[key] !== null && typeof data[key] === "object") data[key] = JSON.stringify(data[key]);
    });
    return qs.stringify(data);
  };
}
