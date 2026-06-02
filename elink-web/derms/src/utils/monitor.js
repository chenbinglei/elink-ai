export default class WSMonitor {
  #url;
  #hasRetry = 0;
  #retryCount = 5;
  #listener = {
    onOpen: () => {},
    onClose: () => {},
    onMessage: () => {},
    onError: () => {},
  };
  #instance;
  /**
   * @typedef {Object} Listener
   * @property {Function} onMessage
   * @property {Function} onError
   * @property {Function} onOpen
   * @property {Function} onClose
   * 
   * @typedef {object} WSMonitorOption
   * @property {String} url   链接地址
   * @property {Number} retryCount 重试次数
   * @property {Listener} listener  监听事件集合

   * 
   * @param {WSMonitorOption} options 
   */
  constructor(options) {
    const { url, retryCount = 5, listener = {} } = options || {};
    this.#url = url;
    this.#retryCount = retryCount;
    this.#listener;
    Object.assign(this.#listener, listener);
    //
    Object.freeze(this.#listener);
    setTimeout(() => {
      this.connect();
    });
  }

  /**
   * 链接建立
   */
  #onOpen() {
    this.#hasRetry = 0;
  }
  /**
   * 链接关闭
   */
  #onClose() {
    this.#listener.onClose();
  }
  /**
   * 消息回调
   * @param {*} data
   */
  #onMessage({ data }) {
    let jsonData;
    try {
      jsonData = JSON.parse(data);
    } catch (e) {
      jsonData = {};
    }
    this.#listener.onMessage(jsonData);
  }
  /**
   * 链接出错 断线重连
   * @param {Error} e
   */
  #onError(e) {
    this.#listener.onError(e);
    if (this.#hasRetry > this.#retryCount) {
      return;
    }
    this.#hasRetry++;
    this.connect();
  }

  /**
   * 链接
   */
  connect() {
    this.#hasRetry++;
    try {
      const instance = new WebSocket(this.#url);
      instance.onclose = (...arg) => this.#onClose?.(...arg);
      instance.onopen = (...arg) => this.#onOpen?.(...arg);
      instance.onmessage = (...arg) => this.#onMessage?.(...arg);
      instance.onerror = (...arg) => this.#onError?.(...arg);
      this.#instance = instance;
    } catch (e) {
      this.#listener.onError?.(e);
    }
  }
  /**
   * 断开链接
   */
  disconnect() {
    this.#instance?.close();
  }
  /**
   *发送消息
   * @param {Object} data
   */
  send(data) {
    this.#instance?.send(JSON.stringify(data));
  }
}
