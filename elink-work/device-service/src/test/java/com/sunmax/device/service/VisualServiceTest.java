package com.sunmax.device.service;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dao.access.GraphDao;
import com.sunmax.device.dao.access.GraphTypeDao;
import com.sunmax.device.entity.access.GraphEntity;
import com.sunmax.device.entity.access.GraphTypeEntity;
import com.sunmax.device.service.impl.VisualServiceImpl;
import com.sunmax.device.vo.GraphChangeVo;
import com.sunmax.device.vo.GraphTypeChangeVo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Example;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("VisualService 单元测试")
class VisualServiceTest {

    @Mock private GraphTypeDao graphTypeDao;
    @Mock private GraphDao graphDao;

    @InjectMocks private VisualServiceImpl visualService;

    @Test
    @DisplayName("保存图形分类-新增时编码已存在返回错误")
    void saveGraphType_new_codeExists_returnsError() {
        GraphTypeChangeVo vo = new GraphTypeChangeVo();
        vo.setCode("GT001");
        vo.setName("测试分类");

        GraphTypeEntity existing = new GraphTypeEntity();
        existing.setId("gt-001");
        existing.setCode("GT001");
        when(graphTypeDao.findAll(any(Example.class))).thenReturn(List.of(existing));

        ResponseResult<Void> result = visualService.saveGraphType(vo);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("保存图形分类-新增成功")
    void saveGraphType_new_success() {
        GraphTypeChangeVo vo = new GraphTypeChangeVo();
        vo.setCode("GT001");
        vo.setName("测试分类");

        when(graphTypeDao.findAll(any(Example.class))).thenReturn(Collections.emptyList());
        when(graphTypeDao.save(any(GraphTypeEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<Void> result = visualService.saveGraphType(vo);
        assertTrue(result.isSuccess());
        verify(graphTypeDao).save(any(GraphTypeEntity.class));
    }

    @Test
    @DisplayName("保存图形分类-编辑时编码与其他冲突返回错误")
    void saveGraphType_edit_codeConflict_returnsError() {
        GraphTypeChangeVo vo = new GraphTypeChangeVo();
        vo.setId("gt-001");
        vo.setCode("GT001");
        vo.setName("测试分类");

        GraphTypeEntity other = new GraphTypeEntity();
        other.setId("gt-002");
        other.setCode("GT001");
        when(graphTypeDao.findAll(any(Example.class))).thenReturn(List.of(other));

        ResponseResult<Void> result = visualService.saveGraphType(vo);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("保存图形分类-编辑成功")
    void saveGraphType_edit_success() {
        GraphTypeChangeVo vo = new GraphTypeChangeVo();
        vo.setId("gt-001");
        vo.setCode("GT001");
        vo.setName("测试分类");

        GraphTypeEntity existing = new GraphTypeEntity();
        existing.setId("gt-001");
        when(graphTypeDao.findAll(any(Example.class))).thenReturn(List.of(existing));
        when(graphTypeDao.findById("gt-001")).thenReturn(Optional.of(existing));
        when(graphTypeDao.save(any(GraphTypeEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<Void> result = visualService.saveGraphType(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("保存图形分类-编辑时实体不存在返回参数错误")
    void saveGraphType_edit_notFound_returnsParamError() {
        GraphTypeChangeVo vo = new GraphTypeChangeVo();
        vo.setId("gt-001");
        vo.setCode("GT001");

        when(graphTypeDao.findAll(any(Example.class))).thenReturn(Collections.emptyList());
        when(graphTypeDao.findById("gt-001")).thenReturn(Optional.empty());

        ResponseResult<Void> result = visualService.saveGraphType(vo);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("查询图形分类列表-无数据返回空列表")
    void findGraphTypeListByTypeId_empty_returnsEmpty() {
        when(graphTypeDao.findAll(any(Example.class))).thenReturn(Collections.emptyList());

        ResponseResult<?> result = visualService.findGraphTypeListByTypeId("type-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询图形分类列表-有数据返回列表")
    void findGraphTypeListByTypeId_withData_returnsList() {
        GraphTypeEntity entity = new GraphTypeEntity();
        entity.setId("gt-001");
        entity.setName("测试分类");
        entity.setCode("GT001");
        when(graphTypeDao.findAll(any(Example.class))).thenReturn(List.of(entity));

        ResponseResult<?> result = visualService.findGraphTypeListByTypeId("type-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("删除图形分类-成功")
    void deleteGraphTypeById_success() {
        ResponseResult<Void> result = visualService.deleteGraphTypeById("gt-001");
        assertTrue(result.isSuccess());
        verify(graphTypeDao).deleteById("gt-001");
    }

    @Test
    @DisplayName("保存图形-deviceId为空时返回参数错误")
    void saveGraph_emptyDeviceId_returnsParamError() {
        GraphChangeVo vo = new GraphChangeVo();
        vo.setDeviceId("");

        ResponseResult<Void> result = visualService.saveGraph(vo);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("保存图形-新增成功")
    void saveGraph_new_success() {
        GraphChangeVo vo = new GraphChangeVo();
        vo.setDeviceId("dev-001");
        vo.setGraphTypeId("gt-001");

        when(graphDao.save(any(GraphEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<Void> result = visualService.saveGraph(vo);
        assertTrue(result.isSuccess());
        verify(graphDao).save(any(GraphEntity.class));
    }

    @Test
    @DisplayName("保存图形-设为默认时更新其他默认图形")
    void saveGraph_setDefault_updatesOthers() {
        GraphChangeVo vo = new GraphChangeVo();
        vo.setDeviceId("dev-001");
        vo.setIsDefault(1);

        GraphEntity existingDefault = new GraphEntity();
        existingDefault.setId("graph-001");
        existingDefault.setDeviceId("dev-001");
        existingDefault.setIsDefault(1);
        when(graphDao.findAll(any(Example.class))).thenReturn(List.of(existingDefault));
        when(graphDao.save(any(GraphEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<Void> result = visualService.saveGraph(vo);
        assertTrue(result.isSuccess());
        verify(graphDao).saveAll(any());
    }

    @Test
    @DisplayName("保存图形-编辑成功")
    void saveGraph_edit_success() {
        GraphChangeVo vo = new GraphChangeVo();
        vo.setId("graph-001");
        vo.setDeviceId("dev-001");

        GraphEntity existing = new GraphEntity();
        existing.setId("graph-001");
        when(graphDao.findById("graph-001")).thenReturn(Optional.of(existing));
        when(graphDao.save(any(GraphEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<Void> result = visualService.saveGraph(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("删除图形-成功")
    void deleteGraphById_success() {
        ResponseResult<Void> result = visualService.deleteGraphById("graph-001");
        assertTrue(result.isSuccess());
        verify(graphDao).deleteById("graph-001");
    }

    @Test
    @DisplayName("查询图形列表-无数据返回空列表")
    void findGraphListByDeviceId_empty_returnsEmpty() {
        when(graphDao.findAll(any(Example.class))).thenReturn(Collections.emptyList());

        ResponseResult<?> result = visualService.findGraphListByDeviceId("dev-001", 1, 10);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询图形列表-有数据返回列表")
    void findGraphListByDeviceId_withData_returnsList() {
        GraphEntity graph = new GraphEntity();
        graph.setId("graph-001");
        graph.setDeviceId("dev-001");
        graph.setGraphTypeId("gt-001");
        when(graphDao.findAll(any(Example.class))).thenReturn(List.of(graph));

        GraphTypeEntity graphType = new GraphTypeEntity();
        graphType.setId("gt-001");
        graphType.setName("测试分类");
        when(graphTypeDao.findAllById(Set.of("gt-001"))).thenReturn(List.of(graphType));

        ResponseResult<?> result = visualService.findGraphListByDeviceId("dev-001", 1, 10);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询关联图形列表-带图形分类编码过滤")
    void findGraphRelevancyListByDeviceId_withCode_filters() {
        GraphEntity graph = new GraphEntity();
        graph.setId("graph-001");
        graph.setDeviceId("dev-001");
        graph.setGraphTypeId("gt-001");
        when(graphDao.findAll(any(Example.class))).thenReturn(List.of(graph));

        GraphTypeEntity graphType = new GraphTypeEntity();
        graphType.setId("gt-001");
        graphType.setName("测试分类");
        graphType.setCode("GT001");
        when(graphTypeDao.findAllById(Set.of("gt-001"))).thenReturn(List.of(graphType));

        ResponseResult<?> result = visualService.findGraphRelevancyListByDeviceId("dev-001", "GT001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询关联图形列表-编码不匹配返回空列表")
    void findGraphRelevancyListByDeviceId_codeNotMatch_returnsEmpty() {
        GraphEntity graph = new GraphEntity();
        graph.setId("graph-001");
        graph.setDeviceId("dev-001");
        graph.setGraphTypeId("gt-001");
        when(graphDao.findAll(any(Example.class))).thenReturn(List.of(graph));

        GraphTypeEntity graphType = new GraphTypeEntity();
        graphType.setId("gt-001");
        graphType.setCode("GT001");
        when(graphTypeDao.findAllById(Set.of("gt-001"))).thenReturn(List.of(graphType));

        ResponseResult<?> result = visualService.findGraphRelevancyListByDeviceId("dev-001", "OTHER");
        assertTrue(result.isSuccess());
    }
}
