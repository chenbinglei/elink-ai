package com.sunmax.common.util;

import java.util.ArrayList;
import java.util.List;

public class PageUtil {

    public static <T> List<T> page(List<T> items, int pageIndex, int pageSize) {
        if (items == null) {
            return new ArrayList<>();
        }
        int count = items.size();
        if (count == 0 || pageSize <= 0) {
            return new ArrayList<>();
        }

        // 处理负数pageIndex的情况
        if (pageIndex <= 0) {
            pageIndex = 1;
        }

        int totalPage = (count - 1) / pageSize + 1;
        // 对筛选数据进行分页
        if (pageIndex > totalPage) {
            pageIndex = totalPage;
        }
        int begin = (pageIndex - 1) * pageSize;
        int end = Math.min(pageIndex * pageSize, items.size());
        return items.subList(begin, end);
    }


}
