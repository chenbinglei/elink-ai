import ExcelJS from "exceljs";
import FileSaver from 'file-saver';
import {downloadFiles} from "@/utils";
import $filters from "@/common/filters"; // 过滤器
import {ElLoading, ElMessage} from 'element-plus';

export async function exportCustomExcel(tableHeader = [], json = [], fileName = "文件名称", sheetName = "Sheet1") {
    const loading = ElLoading.service({lock: true, text: '正在下载中...', background: 'rgba(0, 0, 0, 0.7)'});

    const workbook = new ExcelJS.Workbook(); //创建工作簿
    const worksheet = workbook.addWorksheet(sheetName, {
        properties: {defaultColWidth: 18} // 默认列宽
    });

    worksheet.columns = tableHeader;  // 表头数据

    let fieldNameCn = [];
    for (let i = 0; i < tableHeader.length; i++) fieldNameCn.push(tableHeader[i].name);
    worksheet.addRow(fieldNameCn); // 添加中文标题

    // 插入数据
    if (json && json.length){
        for (let i = 0; i < json.length; i++){
            // 数据处理
            if(json[i]){
                for (let key in json[i]){
                    let findItem = tableHeader.find(item => item.key === key);
                    if(findItem && findItem.filterName){
                        json[i][key] = $filters[findItem.filterName](json[i][key]);
                    }
                }
            }
            worksheet.addRow(json[i]);
        }
    }

    const buffer = await workbook.xlsx.writeBuffer();
    FileSaver.saveAs(new Blob([buffer], {type: 'application/octet-stream'}), fileName + ".xlsx");

    setTimeout(() => {
        loading.close();
        ElMessage({type: 'success', showClose: true, message: '下载成功！'});
    }, 500);
}


// 导出图表的图片
export async function exportCustomEcharts(domContent,fileName = "文件名称") {
    const loading = ElLoading.service({lock: true, text: '正在下载中...', background: 'rgba(0, 0, 0, 0.7)'});
    try {
        const fileUrl = domContent.value.getDataURL({ pixelRatio: 2 });
        downloadFiles(fileUrl,fileName);
        setTimeout(() => {
            loading.close();
            ElMessage({type: 'success', showClose: true, message: '下载成功！'});
        }, 500);
    } catch (e) {
        setTimeout(() => {
            loading.close();
            ElMessage({type: 'error', showClose: true, message: '下载失败，请重试！'});
        }, 500);
    }
}