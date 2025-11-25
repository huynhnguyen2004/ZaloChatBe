package com.huynh.ZaloCloneBe.exception;

import com.huynh.ZaloCloneBe.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
@ControllerAdvice
public class GlobalException {
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<?>> handleAppException(AppException exception){
        ErrorCode code=exception.getCode();
        ApiResponse response=new ApiResponse();
        response.setCode(code.getCode());
        response.setMessenge(code.getMessenger());
        return ResponseEntity.badRequest().body(response);

    }
}
