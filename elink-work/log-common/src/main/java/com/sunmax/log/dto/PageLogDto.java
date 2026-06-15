package com.sunmax.log.dto;

import com.sunmax.log.util.PageLogUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class PageLogDto<T> {

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

    public PageLogDto(List<T> items, int index, int pageSize) {
        this.items = PageLogUtil.page(items, index, pageSize);
        this.pageSize = pageSize;
        this.index = index;
        this.totalSize = items.size();
    }

    public PageLogDto(Page<T> page) {
        this.items = page.getContent();
        this.pageSize = page.getSize();
        this.index = page.getNumber();
        this.totalSize = (int)page.getTotalElements();
    }

    public PageLogDto(List<T> items, int index, int pageSize, int totalSize) {
        this.items = items;
        this.pageSize = pageSize;
        this.index = index;
        this.totalSize = totalSize;
    }

}
