package com.sunmax.configure.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

public class MysqlTabToMdUtil {

    private static final String DATABASE = "sunos-configure";
    private static final String JDBC_URL = "jdbc:mysql://192.168.2.252:3306/" + DATABASE + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";


    public static String generateMarkdown(List<String> tableNames) throws SQLException {
        StringBuilder markdown = new StringBuilder();

        // 加载MySQL JDBC驱动（如果尚未加载）
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            return null;
        }

        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            for (String tableName : tableNames) {
                markdown.append("## ").append(tableName).append("\n");
                markdown.append("| 字段名 | 字段中文名 | 字段类型 | 字段长度 | 是否为空 | 默认值 |\n");
                markdown.append("| ------- | ---------- | ------- | ------ | ------ | ------- |\n");

                String sql = "SELECT COLUMN_NAME, COLUMN_COMMENT,DATA_TYPE,COLUMN_TYPE,IS_NULLABLE, COLUMN_DEFAULT " +
                        "FROM INFORMATION_SCHEMA.COLUMNS " +
                        "WHERE TABLE_SCHEMA = '" + DATABASE + "' AND TABLE_NAME = '" + tableName + "'";

                try (ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        markdown.append("| ")
                                .append(rs.getString("COLUMN_NAME"))
                                .append(" | ")
                                .append(rs.getString("COLUMN_COMMENT"))
                                .append(" | ")
                                .append(rs.getString("DATA_TYPE"))
                                .append(" | ")
                                .append(rs.getString("COLUMN_TYPE").contains("(") ? rs.getString("COLUMN_TYPE")
                                        .substring(rs.getString("COLUMN_TYPE").indexOf("(") + 1, rs.getString("COLUMN_TYPE").indexOf(")")) : 0)
                                .append(" | ")
                                .append(rs.getString("IS_NULLABLE").equals("YES") ? "是" : "否")
                                .append(" | ")
                                .append(rs.getString("COLUMN_DEFAULT") == null ? "无" : rs.getString("COLUMN_DEFAULT"))
                                .append(" |\n");
                    }
                }

                markdown.append("\n");
            }

        }

        return markdown.toString();
    }

    public static void main(String[] args) {
        List<String> tables = Arrays.asList("b_data_source","b_graph", "b_graph_source", "b_pel", "b_variable"); // 你可以添加多个表名

        try {
            String markdown = generateMarkdown(tables);
            System.out.println(markdown);
            // 这里你也可以将markdown字符串写入文件或进行其他处理
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

// 注意：将your_database, your_username, your_password, your_table1, your_table2替换为你的实际数据库信息
