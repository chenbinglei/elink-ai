interface WSConnectionOptions {
  onMessage?: (data: string) => void;
  onOpen?: () => void;
  onClose?: () => void;
  onError?: (error: Event) => void;
}

interface WSConnection {
  instance: WebSocket;
  url: string;
  options: WSConnectionOptions;
  listeners: Map<string, EventListener>;
}

export default class WebSocketManager {
  connections: Map<string, WSConnection>;

  constructor() {
    this.connections = new Map();
  }

  addConnection(name: string, url: string, options: WSConnectionOptions = {}): WebSocket {
    this.removeConnection(name);

    const ws = new WebSocket(url);

    this.connections.set(name, {
      instance: ws,
      url,
      options,
      listeners: new Map()
    });

    this.setOnMessage(name, options.onMessage || ((data: string) => {
      console.log(`${name} 收到消息:`, data);
    }));

    ws.onopen = options.onOpen || (() => {
      console.log(`${name} 连接已建立`);
    });

    ws.onclose = options.onClose || (() => {
      console.log(`${name} 连接已关闭`);
    });

    ws.onerror = options.onError || ((error: Event) => {
      console.error(`${name} 错误:`, error);
    });

    return ws;
  }

  setOnMessage(name: string, callback: (data: string) => void): void {
    const connection = this.connections.get(name);
    if (connection) {
      if (connection.listeners.has('message')) {
        connection.instance.removeEventListener('message', connection.listeners.get('message')!);
      }

      const listener = ((event: Event) => {
        callback((event as MessageEvent).data);
      }) as EventListener;
      connection.instance.addEventListener('message', listener);
      connection.listeners.set('message', listener);
    }
  }

  send(name: string, message: string): boolean {
    const connection = this.connections.get(name);
    if (connection && connection.instance.readyState === WebSocket.OPEN) {
      connection.instance.send(message);
      return true;
    }
    return false;
  }

  removeConnection(name: string): void {
    const connection = this.connections.get(name);
    if (connection) {
      connection.instance.close();
      this.connections.delete(name);
    }
  }

  getStatus(name: string): { url: string; readyState: number; state: string } | null {
    const connection = this.connections.get(name);
    return connection ? {
      url: connection.url,
      readyState: connection.instance.readyState,
      state: ['连接中', '已打开', '关闭中', '已关闭'][connection.instance.readyState]
    } : null;
  }

  closeAll(): void {
    this.connections.forEach((_, name) => this.removeConnection(name));
  }
}
