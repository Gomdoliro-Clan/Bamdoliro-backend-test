package com.bamdoliro.assessment.common;

import java.util.function.Supplier;

/** 컨트롤러 계층의 전역 예외 처리를 흉내 낸 클래스. */
public class ApiExceptionHandler {

    public <T> ApiResponse<T> handle(int successStatus, Supplier<T> action) {
        try {
            return ApiResponse.success(successStatus, action.get());
        } catch (BusinessException e) {
            ErrorCode code = e.getErrorCode();
            return ApiResponse.error(code.getHttpStatus(), code.name());
        } catch (Exception e) {
            return ApiResponse.error(500, ErrorCode.INTERNAL_SERVER_ERROR.name());
        }
    }
}
