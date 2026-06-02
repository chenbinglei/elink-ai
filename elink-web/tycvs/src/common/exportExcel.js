import ExcelJS from "exceljs";
import FileSaver from 'file-saver';
import $filters from "@/common/filters"; // 过滤器
import {ElLoading, ElMessage} from 'element-plus';

const excelPassWord = "P@ssW0rd"; // 生成的表格密码
export async function exportCustomExcel(tableHeader = [], json = [], fileName = "文件名称", sheetName = "Sheet1", options = {
    isInsertEmptyData: false,  // 是否生成空数据
    totalNumber: 1000, // 生成空数据的总条数
    isLock: false, // 是否锁定该文件
}) {
    const loading = ElLoading.service({lock: true, text: '正在下载中...', background: 'rgba(0, 0, 0, 0.7)'});

    const workbook = new ExcelJS.Workbook(); //创建工作簿
    const worksheet = workbook.addWorksheet(sheetName, {
        properties: {defaultColWidth: 18} // 默认列宽
    });

    worksheet.columns = tableHeader;  // 表头数据

    // 插入中文标题数据
    let fieldNameCn = [], empty_obj = {};
    for (let i = 0; i < tableHeader.length; i++) {
        fieldNameCn.push(tableHeader[i].name);
        empty_obj[tableHeader[i].key] = "";
    }
    worksheet.addRow(fieldNameCn); // 添加中文标题

    // 插入数据
    if (json && json.length) {
        for (let i = 0; i < json.length; i++) {
            // 数据处理
            if (json[i]) {
                for (let key in json[i]) {
                    let findItem = tableHeader.find(item => item.key === key);
                    if (findItem && findItem.filterName) {
                        let value = $filters[findItem.filterName](json[i][key]);
                        // 修改过滤器文字 充、放
                        if (findItem.disAndCharging) value = $filters.chargingDisText(value, findItem.disAndCharging);
                        json[i][key] = value;
                    }
                }
            }
            worksheet.addRow(json[i]);
        }
    }

    // 插入空数据
    if (options.isInsertEmptyData) {
        let rowNum = (options.totalNumber || 1000) - json.length;
        for (let i = 0; i < rowNum; i++) worksheet.addRow(empty_obj);

        for (let i = 0; i < tableHeader.length; i++) {
            let activeCol = worksheet.getColumn(tableHeader[i].key);

            // 遍历此列中的所有单元格，包括空单元格
            activeCol.eachCell({includeEmpty: true}, (cell, rowNumber) => {
                if (rowNumber > 1) {

                    if (tableHeader[i].reaType === 1) {
                        cell.dataValidation = {
                            type: 'decimal',
                            allowBlank: true,
                            operator: 'between',
                            showInputMessage: true,
                            promptTitle: '十进制数字',
                            formulae: [tableHeader[i].options.minValue, tableHeader[i].options.maxValue],
                            prompt: `请输入${tableHeader[i].options.minValue}~${tableHeader[i].options.maxValue}之间的数字`
                        }
                    }

                    // 下拉列表
                    if (tableHeader[i].reaType === 3 || tableHeader[i].reaType === 5) {
                        if (tableHeader[i].options && tableHeader[i].options.length) {
                            // console.log('Cell ' + rowNumber + '-' + ' = ' + cell.value);
                            cell.dataValidation = {
                                type: 'list',
                                allowBlank: true, // 允许为空
                                showErrorMessage: true,
                                errorStyle: 'error',
                                formulae: [`"${tableHeader[i].options.map(item => `${item.name}`).join(',')}"`]
                                // formulae: ['"One,Two,Three,Four"']
                            };
                        }
                    }

                    cell.protection = {locked: false}; // 锁定
                }
            });
        }
    }

    // // 遍历工作表中具有值的所有行
    // worksheet.eachRow((row, rowNumber) => {
    //     // 连续遍历所有单元格
    //     row.eachCell({ includeEmpty: true }, (cell, colNumber) => {
    //         // console.log('Cell ' + rowNumber + '-' + colNumber + ' = ' + cell.value);
    //         if (rowNumber > 2 && (colNumber < 3 || colNumber > 6)) {
    //             cell.protection = {locked: false};// 锁定
    //         }
    //     })
    // });

    // 锁定文档，防止用户操作不该操作的列
    if (options.isLock) {
        await worksheet.protect(excelPassWord, {
            sort: true,  //允许用户对数据进行排序
            selectLockedCells: false, //允许用户选择锁定的单元格
        });
    }

    const buffer = await workbook.xlsx.writeBuffer();
    FileSaver.saveAs(new Blob([buffer], {type: 'application/octet-stream'}), fileName + ".xlsx");

    setTimeout(() => {
        loading.close();
        ElMessage({type: 'success', showClose: true, message: '下载成功！'});
    }, 500);
}