/**
 * 二次封装localStorage，实现追加key前缀
 */

const _key_pre_ = `IEMS_PF_`;

export function setItem(key: string, val: string): void {
  return window.localStorage.setItem(_key_pre_ + key, val);
}

export function getItem(key: string): string | null {
  return window.localStorage.getItem(_key_pre_ + key);
}

export function removeItem(key: string): void {
  return window.localStorage.removeItem(_key_pre_ + key);
}

export function clear(): void {
  return window.localStorage.clear();
}

export function key(index: number): string | null {
  return window.localStorage.key(index);
}
