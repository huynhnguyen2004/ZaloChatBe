package com.huynh.ZaloCloneBe.exception;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_EXISTED(1001,"User existed"),
    USER_NOTFOUND(1002,"User NOT FOUND"),
    UNAUTHORIZED(1003,"Login faill"),
    ;

    private int code;
    private String messenger;
}
