package com.sunmax.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

/**
 * @description:
 * @create: 2021-03-11 22:51
 **/
@Slf4j
public class ExcelFormulaUtil {

    /**
     * Sheet 中的每一行
     */
    private static final HSSFRow row;

    private static final FormulaEvaluator formulaEvaluator;

    /**
     * 初始化一个 HSSFWorkbook
     */
    static {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        row = sheet.createRow(0);
        formulaEvaluator = new HSSFFormulaEvaluator(workbook);
    }

    /**
     * 计算值
     *
     * @param formula Excel 中的公式，例如：MAX(56-FLOOR(20/6,1),2)
     * @return 计算值
     */
    public static Double calculateFormula(String formula) {
        // 这里必须新建一个对象，否则只有第一个 formula 才有效。查看 formulaEvaluator.evaluate 的源码。
        HSSFCell cell = row.createCell(0);
        cell.setCellType(CellType.STRING);
        try {
            cell.setCellFormula(formula);
            return DoubleUtil.getToDouble(formulaEvaluator.evaluate(cell).getNumberValue(), 3);
        } catch (FormulaParseException e) {
            return null;
        }
    }

}
