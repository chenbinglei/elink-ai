package com.sunmax.common.dto;

import com.sunmax.common.util.PageUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class PageDto<T> {

    @Schema(description = "当前页数据")
    protected List<T> items = new ArrayList<>();
    /**
     * 每页条数
     */
    @Schema(description = "每页条数")
    protected int pageSize;
    /**
     * 总条数
     */
    @Schema(description = "总条数")
    protected int totalSize;
    /**
     * 第几页
     */
    @Schema(description = "第几页")
    protected int index;

    public PageDto(List<T> items, int index, int pageSize) {
        this.items = PageUtil.page(items, index, pageSize);
        this.pageSize = pageSize;
        this.index = index;
        this.totalSize = items.size();
    }

    public PageDto(Page<T> page) {
        this.items = page.getContent();
        this.pageSize = page.getSize();
        this.index = page.getNumber();
        this.totalSize = (int)page.getTotalElements();
    }

    public PageDto(List<T> items, int index, int pageSize, int totalSize) {
        this.items = items;
        this.pageSize = pageSize;
        this.index = index;
        this.totalSize = totalSize;
    }

}
