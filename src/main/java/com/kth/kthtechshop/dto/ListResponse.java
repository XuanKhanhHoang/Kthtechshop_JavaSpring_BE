package com.kth.kthtechshop.dto;

import java.util.List;

public class ListResponse<T> {
    private final List<T> data;
    private final int totalPage;

    public ListResponse(List<T> data, int totalPage) {
        this.data = data;
        this.totalPage = totalPage;
    }

    public List<T> getData() {
        return data;
    }

    public int getTotalPage() {
        return totalPage;
    }
}
