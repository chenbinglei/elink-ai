export const portNum = ":5000";
const myLocationHost = "localhost";
const locationHost = location.hostname;
const locationProtocol = location.protocol;
const serverIpAddress = process.env.VUE_APP_API_HOST ? `${locationProtocol}//${process.env.VUE_APP_API_HOST}${portNum}` : `${locationProtocol}//${locationHostname}${portNum}`;
const onlineServerIpAddress = `${locationProtocol}//${locationHost}${locationProtocol === "http:" ? portNum : ":21010"}`;

export const requestPath = process.env.NODE_ENV === 'development' ? "/proxy" : (locationHost === myLocationHost ? serverIpAddress : onlineServerIpAddress);
