package com.common.vo;

import lombok.Data;

@Data
public class PageVO {
    private Integer size;
    private Integer totalElements;
    private Integer totalPages;
    private Integer number;
}
