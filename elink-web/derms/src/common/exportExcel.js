import ExcelJS from "exceljs";
import FileSaver from "file-saver";
import $filters from "@/common/filters"; // 过滤器
import { ElLoading, ElMessage } from "element-plus";
import fieldHandleFun from "./directive/exportFieldHandle";

const findColName = (item) => {
  return item.name || item.title || item.key;
};
const flatTableHeader = (tableHeader) => {
  let flatHeader = [];
  for (let i = 0; i < tableHeader.length; i++) {
    const item = tableHeader[i];
    const name = findColName(item); // 中文标题
    if (item.children && item.children.length > 0) {
      const childrenColName = item.children.map((child) => ({
        ...child,
        name: `${name}-${findColName(child)}`,
      }));
      flatHeader.push(...childrenColName);
    } else {
      flatHeader.push(item);
    }
  }
  console.log(flatHeader)
  return flatHeader;
};

// export async function exportCustomExcel (
//   tableHeader = [],
//   json = [],
//   fileName = "文件名称",
//   sheetName = "Sheet1"
// ) {
//   console.log(json)
//   const flatHeader = flatTableHeader(tableHeader);
//   const loading = ElLoading.service({
//     lock: true,
//     text: "正在下载中...",
//     background: "rgba(0, 0, 0, 0.7)",
//   });

//   const workbook = new ExcelJS.Workbook(); //创建工作簿
//   const worksheet = workbook.addWorksheet(sheetName, {
//     properties: { defaultColWidth: 24 }, // 默认列宽
//   });
//   console.log(flatHeader, '表头')
//   worksheet.columns = flatHeader.map((item) => ({
//     header: findColName(item), // 中文标题
//     key: item.key,
//     style: {
//       font: {
//         name: "微软雅黑",
//         size: 14,
//         bold: false,
//         color: { argb: "FF000000" },
//       },
//       alignment: {
//         vertical: "middle",
//         horizontal: "left",
//         wrapText: false,
//       },

//     },
//   })); // 设置表头

//   // 插入数据
//   if (json && json.length) {
//     for (let i = 0; i < json.length; i++) {
//       const rowData = json[i];
//       // 数据处理
//       if (rowData) {
//         // 导出数据拼接处理
//         if (JSON.stringify(flatHeader).indexOf("exportHandleFun") !== -1) {
//           for (let j = 0; j < flatHeader.length; j++) {
//             if (flatHeader[j].exportHandleFun) {
//               rowData[flatHeader[j].key] =
//                 fieldHandleFun[flatHeader[j].exportHandleFun](rowData);
//             } else if (typeof flatHeader[j].formatter === "function") {
//               rowData[flatHeader[j].key] = flatHeader[j].formatter(
//                 rowData[flatHeader[j].key],
//                 rowData
//               );
//             }
//           }
//         }

//         for (let key in rowData) {
//           let findItem = flatHeader.find((item) => item.key === key);
//           if (findItem) {
//             // 数据过滤
//             if (findItem.filterName) {
//               let value = $filters[findItem.filterName](rowData[key]);
//               // 修改过滤器文字 充、放
//               if (findItem.disAndCharging) {
//                 value = $filters.chargingDisText(
//                   value,
//                   findItem.disAndCharging
//                 );
//               }
//               rowData[key] = value ?? "--";
//             } else {
//               // rowData[key] = rowData[key] ?? "--";
//             }
//           }
//         }
//         const row = worksheet.addRow(rowData);
//         row.eachCell((cell, colNumber) => {
//           cell.style = {
//             font: {
//               name: "微软雅黑",
//               size: 12,
//               bold: false,
//               color: { argb: "FF000000" },
//             },
//             alignment: {
//               vertical: "middle",
//               horizontal: "left",
//               wrapText: false,
//             },
//           };
//         });
//       }
//       worksheet.addRow(rowData);
//     }
//   }

//   const buffer = await workbook.xlsx.writeBuffer();
//   FileSaver.saveAs(
//     new Blob([buffer], { type: "application/octet-stream" }),
//     fileName + ".xlsx"
//   );

//   setTimeout(() => {
//     loading.close();
//     ElMessage({ type: "success", showClose: true, message: "下载成功！" });
//   }, 500);
// }
export async function exportCustomExcel (
  tableHeader = [],
  json = [],
  fileName = "文件名称",
  sheetName = "Sheet1"
) {
  console.log(json);
  const flatHeader = flatTableHeader(tableHeader);
  const loading = ElLoading.service({
    lock: true,
    text: "正在下载中...",
    background: "rgba(0, 0, 0, 0.7)",
  });

  const workbook = new ExcelJS.Workbook(); // 创建工作簿
  const worksheet = workbook.addWorksheet(sheetName, {
    properties: { defaultColWidth: 24 }, // 默认列宽
  });

  console.log(flatHeader, '表头');

  // 设置表头样式（字体、大小、背景色）
  worksheet.columns = flatHeader.map((item) => ({
    header: findColName(item), // 中文标题
    key: item.key,
    width: 24, // 设置列宽
    style: {
      font: {
        name: "微软雅黑",
        size: 14,
        bold: false, // 表头加粗
        // color: { argb: "FF000000" },
        color: { argb: "FFFFFFFF" }, // 白色字体
      },
      fill: {
        type: "pattern",
        pattern: "solid",

      },
      alignment: {
        vertical: "middle",
        horizontal: "left",
        wrapText: false,
      },
    },
  }));
  // 只给表头行（第1行）添加背景色
  const headerRow = worksheet.getRow(1);
  headerRow.fill = {
    type: "pattern",
    pattern: "solid",
    fgColor: { argb: "FF0070C0" }, // 蓝色背景
  };
  // 插入数据行
  if (json && json.length) {
    for (let i = 0; i < json.length; i++) {
      const rowData = json[i];
      // 数据处理
      if (rowData) {
        // 导出数据拼接处理
        if (JSON.stringify(flatHeader).indexOf("exportHandleFun") !== -1) {
          for (let j = 0; j < flatHeader.length; j++) {
            if (flatHeader[j].exportHandleFun) {
              rowData[flatHeader[j].key] =
                fieldHandleFun[flatHeader[j].exportHandleFun](rowData);
            } else if (typeof flatHeader[j].formatter === "function") {
              rowData[flatHeader[j].key] = flatHeader[j].formatter(
                rowData[flatHeader[j].key],
                rowData
              );
            }
          }
        }

        for (let key in rowData) {
          let findItem = flatHeader.find((item) => item.key === key);
          if (findItem) {
            // 数据过滤
            if (findItem.filterName) {
              let value = $filters[findItem.filterName](rowData[key]);
              // 修改过滤器文字 充、放
              if (findItem.disAndCharging) {
                value = $filters.chargingDisText(
                  value,
                  findItem.disAndCharging
                );
              }
              rowData[key] = value ?? "--";
            } else {
              // rowData[key] = rowData[key] ?? "--";
            }
          }
        }

        // 设置数据行样式（字体、大小）
        const row = worksheet.addRow(rowData);
        row.eachCell((cell, colNumber) => {
          cell.style = {
            font: {
              name: "微软雅黑",
              size: 12,
              bold: false,
              color: { argb: "FF000000" },
            },
            alignment: {
              vertical: "middle",
              horizontal: "left",
              wrapText: false,
            },
          };
        });
      }
    }
  }



  // 导出文件
  const buffer = await workbook.xlsx.writeBuffer();
  const blob = new Blob([buffer], {
    type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
  });
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = `${fileName}.xlsx`;
  a.click();
  window.URL.revokeObjectURL(url);

  loading.close();
}
/**
 * 导出多 Sheet Excel 文件
 * @param {Array} sheets - 工作表配置数组，每个元素包含：
 *   - tableHeader {Array} 表头配置
 *   - json {Array} 数据
 *   - sheetName {String} 工作表名称
 * @param {String} fileName - 导出文件名（不含扩展名）
 */

/**
 * 导出多 Sheet Excel 文件
 * @param {Array} sheets - 工作表配置数组，每个元素包含：
 *   - tableHeader {Array} 表头配置（支持树形结构，会被 flatTableHeader 拍平）
 *   - json {Array} 数据数组
 *   - sheetName {String} 工作表名称
 * @param {String} fileName - 导出文件名（不含扩展名）
 */

export async function exportMultiSheetExcel(sheets, fileName = "文件名称") {
  // 显示加载提示
  const loading = ElLoading.service({
    lock: true,
    text: "正在下载中...",
    background: "rgba(0, 0, 0, 0.7)",
  });

  try {
    const workbook = new ExcelJS.Workbook();

    // 遍历每个工作表配置
    for (const sheet of sheets) {
      const { tableHeader = [], json = [], sheetName = "Sheet1" } = sheet;

      // 1. 拍平表头（原始表头可能包含 children）
      let flatHeader = flatTableHeader(tableHeader);

      // 2. 关键修复：确保每个表头项都有 key 和 header
      flatHeader = flatHeader.map(item => {
        // 优先使用 prop 作为 key，如果没有则回退到 item.key（可能不存在）
        const key = item.prop || item.key;
        // 标题：优先使用 label，其次使用 item.name（由 flatTableHeader 生成），再调用 findColName，最后回退到 key
        const header = item.label || item.name || findColName(item) || key || '未知列';

        if (!key) {
          console.warn('表头项缺少 key 或 prop，该列将被忽略', item);
          return null; // 过滤掉无效项
        }

        return {
          ...item,      // 保留原有自定义属性（如 filterName, exportHandleFun, disAndCharging 等）
          key,          // 确保 key 存在
          header,       // 添加 header 方便后续使用
        };
      }).filter(Boolean); // 移除被过滤掉的项

      // 调试：确认 flatHeader 已正确转换
      console.log('处理后 flatHeader:', flatHeader);

      // 创建工作表
      const worksheet = workbook.addWorksheet(sheetName, {
        properties: { defaultColWidth: 24 },
      });

      // 3. 设置列定义（使用转换后的 key 和 header）
      worksheet.columns = flatHeader.map((item) => ({
        header: item.header,
        key: item.key,
        width: item.width || 24,
      }));

      // 4. 设置表头行（第1行）样式
      const headerRow = worksheet.getRow(1);
      headerRow.fill = {
        type: "pattern",
        pattern: "solid",
        fgColor: { argb: "FF0070C0" }, // 蓝色背景
      };
      headerRow.font = {
        name: "微软雅黑",
        size: 14,
        bold: false,
        color: { argb: "FFFFFFFF" }, // 白色字体
      };
      headerRow.alignment = {
        vertical: "middle",
        horizontal: "left",
        wrapText: false,
      };

      // 5. 插入数据行
      if (json && json.length) {
        for (let i = 0; i < json.length; i++) {
          // 浅拷贝数据，避免修改原始对象
          const rowData = { ...json[i] };

          // 5.1 处理自定义导出函数（如 exportHandleFun、formatter）
          // 注意：这里使用 JSON.stringify 判断是否存在 exportHandleFun 只是为了性能优化，可省略
          if (JSON.stringify(flatHeader).includes("exportHandleFun")) {
            for (let j = 0; j < flatHeader.length; j++) {
              const headerItem = flatHeader[j];
              if (headerItem.exportHandleFun) {
                // 调用全局 fieldHandleFun 中的函数
                rowData[headerItem.key] = fieldHandleFun[headerItem.exportHandleFun](rowData);
              } else if (typeof headerItem.formatter === "function") {
                rowData[headerItem.key] = headerItem.formatter(
                  rowData[headerItem.key],
                  rowData
                );
              }
            }
          }

          // 5.2 数据过滤器处理（filterName）
          for (let key in rowData) {
            const findItem = flatHeader.find((item) => item.key === key);
            if (findItem?.filterName) {
              let value = $filters[findItem.filterName](rowData[key]);
              if (findItem.disAndCharging) {
                value = $filters.chargingDisText(value, findItem.disAndCharging);
              }
              rowData[key] = value ?? "--"; // 若过滤后为 null/undefined，则显示 "--"
            }
          }

          // 5.3 添加数据行
          const row = worksheet.addRow(rowData);

          // 5.4 设置数据行单元格样式
          row.eachCell((cell) => {
            cell.font = {
              name: "微软雅黑",
              size: 12,
              bold: false,
              color: { argb: "FF000000" }, // 黑色字体
            };
            cell.alignment = {
              vertical: "middle",
              horizontal: "left",
              wrapText: false,
            };
          });
        }
      }
    }

    // 6. 生成并下载 Excel 文件
    const buffer = await workbook.xlsx.writeBuffer();
    const blob = new Blob([buffer], {
      type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = `${fileName}.xlsx`;
    a.click();
    window.URL.revokeObjectURL(url);

  } catch (error) {
    console.error('导出过程中发生错误:', error);
    // 可根据需要显示错误提示
    // ElMessage.error('导出失败，请查看控制台');
  } finally {
    // 关闭加载提示
    loading.close();
  }
}