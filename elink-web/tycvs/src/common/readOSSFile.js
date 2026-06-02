import OSS from 'ali-oss';
import { ElMessage } from "element-plus";

const FILE_DIR = "sunos/file/";  // 文件路径
export function readOSSFile (data) {
    // 配置OSS客户端
    const client = new OSS({
        secure: true,
        bucket: import.meta.env.VITE_OSS_BUCKET_NAME,
        region: import.meta.env.VITE_OSS_REGION,
        accessKeyId: import.meta.env.VITE_ALIYUN_ACCESS_KEY_ID,
        accessKeySecret: import.meta.env.VITE_ALIYUN_ACCESS_KEY_SECRET,
    });

    return new Promise(async (resolve, reject) => {
        try {
            // 读取OSS上的文件
            const result = await client.get(FILE_DIR + data.fileName);
        
            resolve({ message: '文件内容获取成功！', code: 20000, data: result.content.toString() });
        } catch (e) {
            console.error(e);
            reject({ message: '文件内容获取失败！', code: 10004 });
            ElMessage({ message: "文件内容获取失败！", showClose: true, type: "error", duration: 5000 });
        }
    })
}

// /sunos/file/1731218553707.json
// 调用函数读取文件
// readOSSFile('1731218553707.json');