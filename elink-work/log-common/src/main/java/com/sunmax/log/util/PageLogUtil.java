package com.sunmax.log.util;

import java.util.List;

public class PageLogUtil {

    static public <T> List<T> page(List<T> items, int pageIndex, int pageSize) {
        if (items == null) {
            return null;
        }
        int count = items.size();
        if (count <= 0 || pageSize == 0) {
            return items;
        }

        int totalPage = (count - 1) / pageSize + 1;
        // 对筛选数据进行分页
        if (pageIndex > totalPage) {
            pageIndex = totalPage;
        }
        int begin = (pageIndex - 1) * pageSize;
        int end = pageIndex * pageSize > items.size() ? items.size() : begin + pageSize;
        return items.subList(begin, end);
    }

}
