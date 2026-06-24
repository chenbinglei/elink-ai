// 配置默认服务器域名地址
let domainName = '',webSocket = "",staticPath = "";
const serverUrl = 'online'; //真实服务器地址

switch (serverUrl) {
	case 'qz':
	    webSocket = 'ws://192.168.2.251:60010';
		domainName = 'http://192.168.2.251:60010';
		staticPath = 'https://os.enlinkitech.com';
		break;
	case 'bl':
	    webSocket = 'ws://192.168.2.142:60010';
		domainName = 'http://192.168.2.142:60010';
		staticPath = 'https://os.enlinkitech.com';
		break;
	case 'cs':
		domainName = 'http://192.168.2.155:5000';
		// domainName = 'http://121.41.109.130:5000'
		staticPath = 'https://os.enlinkitech.com';
		// staticPath = 'https://smiot.sunmaxxtech.com';
		break;
	case 'online':
		domainName = 'https://os.enlinkitech.com';
		// domainName='https://os.shengmantech.com/'
		break;
	default:
		domainName = '';
		break;
}

if(!staticPath)staticPath = domainName;
if(!webSocket)webSocket = domainName.replace('http', 'ws');

uni.setStorageSync('BASE_URL', domainName); //接口请求地址
uni.setStorageSync('WEB_SOCKET', webSocket); //websocket请求地址
uni.setStorageSync('STATIC_PATH', staticPath); //静态资源请求地址

//能链E充  wxcbbd98f921327cfc


//红船享充  wxe02dfd7da2097c23
