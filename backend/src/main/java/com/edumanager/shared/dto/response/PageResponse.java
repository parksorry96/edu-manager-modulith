package com.edumanager.shared.dto.response;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.data.domain.Page;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        boolean hasNext,
        boolean hasPrevious
) {
    // Spring Data Page 객체로부터 생성
    public static <T> PageResponse<T> of(Page<T> page){
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.hasNext(),
                page.hasPrevious()
        );
    }

    // 수동 페이지 생성
    public static <T> PageResponse<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        return new PageResponse<>(
                content,
                page,
                size,
                totalElements,
                totalPages,
                page == 0,                        // first
                page == totalPages - 1,           // last
                page < totalPages - 1,            // hasNext
                page > 0                          // hasPrevious
        );
    }



}
