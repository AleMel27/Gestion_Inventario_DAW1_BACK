package com.gestionInventario.dtos.response;

import java.util.List;

import lombok.Data;

@Data
public class PageDTO<T> {

    private List<T> items;
    private long totalItems;
    private int totalPages;
    private int page;
    private int size;

    public PageDTO(List<T> items, long totalItems, int totalPages, int page, int size) {
        this.items = items;
        this.totalItems = totalItems;
        this.totalPages = totalPages;
        this.page = page;
        this.size = size;
    }
}
