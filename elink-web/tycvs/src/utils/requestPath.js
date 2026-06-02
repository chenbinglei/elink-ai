export const portNum = ":5000";
const myLocationHost = "localhost";
const locationHost = location.hostname;
const locationProtocol = location.protocol;
const serverIpAddress = `${locationProtocol}//192.168.2.158${portNum}`;
const onlineServerIpAddress = `${locationProtocol}//${locationHost}${locationProtocol === "http:" ? portNum : ":21010"}`;

export const requestPath = process.env.NODE_ENV === 'development' ? "/proxy" : (locationHost === myLocationHost ? serverIpAddress : onlineServerIpAddress);
