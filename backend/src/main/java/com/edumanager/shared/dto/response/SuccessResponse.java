package com.edumanager.shared.dto.response;

import org.springframework.data.domain.Page;

public class SuccessResponse {
    // 데이터와 함께 성공 응답
    public static <T> ApiResponse<T> of(T data) {
        return ApiResponse.success(data);
    }

    // 메시지와 데이터를 함께 포함한 성공 응답
    public static <T> ApiResponse<T> of(String message, T data) {
        return ApiResponse.success(message, data);
    }

    // 메시지만 포함한 성공 응답 (데이터 없음)
    public static <T> ApiResponse<T> of(String message) {
        return ApiResponse.success(message);
    }

    // 페이징 데이터를 포함한 성공 응답
    public static <T> ApiResponse<PageResponse<T>> of(PageResponse<T> pageData) {
        return ApiResponse.success(pageData);
    }

    // Spring Data Page를 바로 ApiResponse로 변환
    public static <T> ApiResponse<PageResponse<T>> of(Page<T> page) {
        return ApiResponse.success(PageResponse.of(page));
    }

    // 생성/수정/삭제 등 상태 메시지가 중요한 경우
    public static <T> ApiResponse<T> created(T data) {
        return ApiResponse.success("리소스가 성공적으로 생성되었습니다.", data);
    }

    public static <T> ApiResponse<T> updated(T data) {
        return ApiResponse.success("리소스가 성공적으로 수정되었습니다.", data);
    }

    public static <T> ApiResponse<T> deleted() {
        return ApiResponse.success("리소스가 성공적으로 삭제되었습니다.");
    }
}
