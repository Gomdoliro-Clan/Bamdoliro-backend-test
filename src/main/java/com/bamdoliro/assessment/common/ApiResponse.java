package com.bamdoliro.assessment.common;

/** HTTP 응답을 흉내 낸 값 객체. status 는 HTTP 상태코드, code 는 에러 코드 문자열. */
public record ApiResponse<T>(int status, String code, T data) {

    public static <T> ApiResponse<T> success(int status, T data) {
        return new ApiResponse<>(status, "OK", data);
    }

    public static <T> ApiResponse<T> error(int status, String code) {
        return new ApiResponse<>(status, code, null);
    }
}
