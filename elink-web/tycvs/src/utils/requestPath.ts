export const portNum = ":5000";
const myLocationHost = "localhost";
const locationHost = location.hostname;
const locationProtocol = location.protocol;
const serverIpAddress = import.meta.env.VITE_API_HOST ? `${locationProtocol}//${import.meta.env.VITE_API_HOST}${portNum}` : `${locationProtocol}//${locationHostname}${portNum}`;
const onlineServerIpAddress = `${locationProtocol}//${locationHost}${locationProtocol === "http:" ? portNum : ":5000"}`;

export const requestPath = import.meta.env.DEV ? "/proxy" : (locationHost === myLocationHost ? serverIpAddress : onlineServerIpAddress);
