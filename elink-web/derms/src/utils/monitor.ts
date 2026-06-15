interface WSMonitorListener {
  onMessage: (data: Record<string, unknown>) => void;
  onError: (e: Event) => void;
  onOpen: () => void;
  onClose: () => void;
}

interface WSMonitorOptions {
  url: string;
  retryCount?: number;
  listener?: Partial<WSMonitorListener>;
}

export default class WSMonitor {
  #url: string;
  #hasRetry: number = 0;
  #retryCount: number;
  #listener: WSMonitorListener;
  #instance: WebSocket | null = null;

  constructor(options: WSMonitorOptions) {
    const { url, retryCount = 5, listener = {} } = options || {};
    this.#url = url;
    this.#retryCount = retryCount;
    this.#listener = {
      onMessage: () => {},
      onClose: () => {},
      onOpen: () => {},
      onError: () => {},
    };
    Object.assign(this.#listener, listener);
    Object.freeze(this.#listener);
    setTimeout(() => {
      this.connect();
    });
  }

  #onOpen(): void {
    this.#hasRetry = 0;
  }

  #onClose(): void {
    this.#listener.onClose();
  }

  #onMessage({ data }: MessageEvent): void {
    let jsonData: Record<string, unknown>;
    try {
      jsonData = JSON.parse(data);
    } catch (_e) {
      jsonData = {};
    }
    this.#listener.onMessage(jsonData);
  }

  #onError(_e: Event): void {
    this.#listener.onError(_e);
    if (this.#hasRetry > this.#retryCount) {
      return;
    }
    this.#hasRetry++;
    this.connect();
  }

  connect(): void {
    this.#hasRetry++;
    try {
      const instance = new WebSocket(this.#url);
      instance.onclose = (...arg) => this.#onClose?.(...arg);
      instance.onopen = (...arg) => this.#onOpen?.(...arg);
      instance.onmessage = (...arg) => this.#onMessage?.(...arg);
      instance.onerror = (...arg) => this.#onError?.(...arg);
      this.#instance = instance;
    } catch (e) {
      this.#listener.onError?.(e as Event);
    }
  }

  disconnect(): void {
    this.#instance?.close();
  }

  send(data: Record<string, unknown>): void {
    this.#instance?.send(JSON.stringify(data));
  }
}
