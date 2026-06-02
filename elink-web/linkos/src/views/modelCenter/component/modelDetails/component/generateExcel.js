import ExcelJS from "exceljs";
import FileSaver from 'file-saver';
import {ElLoading, ElMessage} from 'element-plus';

const excelPassWord = "P@ssW0rd"; // 生成的表格密码

export async function exportExcelTable(tableHeader = [], json = [], fileName = "文件名称", sheetName = "Sheet1",maxTableNum = 1000) {
    const loading = ElLoading.service({lock: true, text: '正在下载中...', background: 'rgba(0, 0, 0, 0.7)'});
    const workbook = new ExcelJS.Workbook(); //创建工作簿
    const worksheet = workbook.addWorksheet(sheetName, {
        properties: {defaultColWidth: 18} // 默认列宽
    });

    worksheet.columns = tableHeader;  // 表头数据

    let fieldNameCn = [];
    for (let i = 0; i < tableHeader.length; i++) {
        fieldNameCn.push(tableHeader[i].fieldNameCn);
    }

    worksheet.addRow(fieldNameCn); // 添加中文标题
    const row = worksheet.getRow(1);
    row.hidden = true;// 隐藏行

    // 插入数据
    if (json && json.length) for (let i = 0; i < json.length; i++) worksheet.addRow(json[i]);

    //添加行数（1100）
    let rowNum = maxTableNum - json.length;
    for (let i = 0; i < rowNum; i++) worksheet.addRow({});


    for (let i = 0; i < tableHeader.length; i++) {
        let activeCol = worksheet.getColumn(tableHeader[i].key);

        // 位置 格式
        if(tableHeader[i].reaType === 2 || tableHeader[i].reaType === 4)activeCol.numFmt = '@';

        // 遍历此列中的所有单元格，包括空单元格
        activeCol.eachCell({includeEmpty: true}, (cell, rowNumber) => {
            if (rowNumber > 2) {

                if(tableHeader[i].reaType === 1){
                    cell.dataValidation = {
                        type: 'decimal',
                        allowBlank: true,
                        operator: 'between',
                        showInputMessage: true,
                        promptTitle: '十进制数字',
                        formulae: [tableHeader[i].options.minValue, tableHeader[i].options.maxValue],
                        prompt: `请输入${ tableHeader[i].options.minValue }~${ tableHeader[i].options.maxValue }之间的数字`
                    }
                }

                if(tableHeader[i].reaType === 3 || tableHeader[i].reaType === 5){
                    if (tableHeader[i].options && tableHeader[i].options.length) {
                        // console.log('Cell ' + rowNumber + '-' + ' = ' + cell.value);
                        cell.dataValidation = {
                            type: 'list',
                            allowBlank: true, // 允许为空
                            showErrorMessage: true,
                            errorStyle: 'error',
                            formulae: [`"${ tableHeader[i].options.map(item => `${ item.name }`).join(',') }"`]
                        };
                    }
                }

                // 日期处理
                if(tableHeader[i].reaType === 6){
                    cell.numFmt = setNumFmtFun(tableHeader[i].options);
                    cell.dataValidation = {
                        type: 'date',
                        allowBlank: true,
                        operator: 'lessThan',
                        showErrorMessage: true,
                        formulae: [new Date(2424,0,1,23,59,59)]
                    }
                }

                cell.protection = { locked: false };// 锁定
            }
        });
    }

    // 锁定文档，防止用户操作不该操作的列
    await worksheet.protect(excelPassWord, {
        sort: true,  //允许用户对数据进行排序
        insertRows: true, // 允许用户插入行
        deleteRows: true, // 允许用户删除行
        autoFilter: true, // 	允许用户过滤表中的数据
        selectLockedCells: false, //允许用户选择锁定的单元格
    });

    const buffer = await workbook.xlsx.writeBuffer();
    FileSaver.saveAs(new Blob([buffer], {type: 'application/octet-stream'}), fileName + ".xlsx");

    setTimeout(() => {
        loading.close();
        ElMessage({type: 'success', showClose: true, message: '下载成功！'});
    }, 500);
}


export function setNumFmtFun (timeFormat){
    let format = "";
    switch (timeFormat) {
        case "yyyy-MM-dd":
            format = "yyyy-mm-dd";
            break
        case "yyyy-MM-dd HH:mm":
            format = "yyyy-mm-dd hh:mm";
            break
        case "yyyy-MM-dd HH:mm:ss":
            format = "yyyy-mm-dd hh:mm:ss";
            break
        default:
            format = "yyyy-mm-dd";
    }

    return format
}
