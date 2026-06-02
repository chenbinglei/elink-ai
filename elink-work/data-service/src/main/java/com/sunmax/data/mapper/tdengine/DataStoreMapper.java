package com.sunmax.data.mapper.tdengine;

import com.sunmax.data.model.TableFieldModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface DataStoreMapper {

    //根据表名称查询表是否存在
    Map<String, Object> findTableIfExists(@Param("tableName") String tableName);

    //根据表名称查询表字段名列表
    List<TableFieldModel> findFieldNameTypeList(@Param("tableName") String tableName);

    //创建通道表
    void createTable(@Param("tableName") String tableName, @Param("fieldNameTypeMap") Map<String, String> fieldNameTypes);

    //批量添加通道的多个列
    void addTableField(@Param("tableName") String tableName, @Param("fieldName") String fieldName, @Param("fieldType") String fieldType);

    //批量修改表的多个列
    void updateTableField(@Param("tableName") String tableName, @Param("fieldName") String fieldName, @Param("fieldType") String fieldType);

    //批量删除表的列
    void deleteTableField(@Param("tableName") String tableName, @Param("fieldName") String fieldName);

    //删除表
    void deleteTable(@Param("tableName") String tableName);

    //批量添加表数据
    void batchAddTableData(@Param("tableName") String tableName, @Param("fieldNameValueMap") Map<String, Object> fieldNameValueMap);

    //查询表数据
    List<Map<String, Object>> queryTableDataList(@Param("tableName") String tableName, @Param("columnNames") List<String> columnNames,
                                                 @Param("startTime") String startTime, @Param("endTime") String endTime,
                                                 @Param("timeInterval") String timeInterval, @Param("limitSize") Integer limitSize);


    List<Map<String, Object>> queryfirstTableDataList(@Param("tableName") String tableName, @Param("columnNames") List<String> columnNames,
                                                      @Param("startTime") String startTime, @Param("endTime") String endTime,
                                                      @Param("timeInterval") String timeInterval);

    List<Map<String, Object>> querySourceTableDataList(@Param("tableName") String tableName, @Param("columnNames") List<String> columnNames,
                                                       @Param("startTime") String startTime, @Param("endTime") String endTime);

    List<Map<String, Object>> queryCountFunTableDataList(@Param("tableName") String tableName, @Param("columnNames") List<String> columnNames,
                                                         @Param("startTime") String startTime, @Param("endTime") String endTime,
                                                         @Param("timeInterval") String timeInterval, @Param("cuntFun") String cuntFun);

    List<Map<String, Object>> findNodeDifHistoryList(@Param("tableName") String tableName, @Param("columnNames") List<String> columnNames,
                                                     @Param("startTime") String startTime, @Param("endTime") String endTime,
                                                     @Param("timeInterval") String timeInterval);

    List<String> findAllTableIfExists();

}
