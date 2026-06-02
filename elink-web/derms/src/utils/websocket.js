
export default class WebSocketManager {
  constructor() {
    this.connections = new Map();
  }
  // 创建新连接
  addConnection(name, url, options = {}) {
    // 如果已存在，先关闭
    this.removeConnection(name);
    
    const ws = new WebSocket(url);
    
    // 存储连接信息
    this.connections.set(name, {
      instance: ws,
      url,
      options,
      listeners: new Map()
    });
    
    // 设置默认事件监听
    this.setOnMessage(name, options.onMessage || ((data) => {
      console.log(`${name} 收到消息:`, data);
    }));
    
    ws.onopen = options.onOpen || (() => {
      console.log(`${name} 连接已建立`);
    });
    
    ws.onclose = options.onClose || (() => {
      console.log(`${name} 连接已关闭`);
    });
    
    ws.onerror = options.onError || ((error) => {
      console.error(`${name} 错误:`, error);
    });
    
    return ws;
  }

  // 设置消息处理器
  setOnMessage(name, callback) {
    const connection = this.connections.get(name);
    if (connection) {
      // 移除旧监听器
      if (connection.listeners.has('message')) {
        connection.instance.removeEventListener('message', connection.listeners.get('message'));
      }
      
      // 添加新监听器
      const listener = (event) => {
        callback(event.data)
      };
      connection.instance.addEventListener('message', listener);
      connection.listeners.set('message', listener);
    }
  }

  // 发送消息
  send(name, message) {
    const connection = this.connections.get(name);
    if (connection && connection.instance.readyState === WebSocket.OPEN) {
      connection.instance.send(message);
      return true;
    }
    return false;
  }

  // 关闭连接
  removeConnection(name) {
    const connection = this.connections.get(name);
    if (connection) {
      connection.instance.close();
      this.connections.delete(name);
    }
  }

  // 获取连接状态
  getStatus(name) {
    const connection = this.connections.get(name);
    return connection ? {
      url: connection.url,
      readyState: connection.instance.readyState,
      state: ['连接中', '已打开', '关闭中', '已关闭'][connection.instance.readyState]
    } : null;
  }
  // 关闭所有连接
  closeAll() {
    this.connections.forEach((_, name) => this.removeConnection(name));
  }
}