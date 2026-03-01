package com.huynh.ZaloCloneBe.exception;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_EXISTED(1001,"SĐT đã tồn tại"),
    USER_NOTFOUND(1002,"Tài khoản không tồn tại"),
    UNAUTHORIZED(1003,"Thông tin đăng nhập sai"),
    PASS_VALID(1014,"Mật khẩu phải có độ dài lớn hơn 5 kí tự và không được chứa kí tự đặc biệt"),
    REQUEST_FRIEND_ERROR(1004,"khong the gui loi moi chinh minh"),
    REQUEST_NOTFOUND(1008,"REQUEST_NOTFOUND"),
    REQUEST_ALREADY_ACCEPTED(1009,"REQUEST_ALREADY_ACCEPTED"),
    REQUEST_CANNOT_CANCEL(1013,"REQUEST_CANNOT_CANCEL"),
    REQUEST_CANNOT_REJECT(1021,"Không thể từ chối"),
    SEND_REQUEST_ERROR(1005,"Da gui loi moi roi"),
    SEND_NOTFOUND(1006,"SEND NOT FOUND"),
    RECEIVE_NOTFOUND(1007,"rECEIVE NOT FOUND"),
    FRIEND_ALREADY(1010,"FRIEND_ALREADY"),
    CONTENT_NULL(1011,"CONTENT NOT NULL"),
    FRIEND_notfound(1012,"FRIEND_not found"),
    MESS_NOTFOUND(1013,"MESS NOT FOUND"),
    CAPTCHA_INVALID(1014,"Capcha không hợp lệ"),
    FILE_EMPTY(1015,"FILE RONG"),
    JUST_IMAGE(1016,"CHI ANH jpg,png"),
    OLDPASS_NULL(1017,"Vui lòng nhập mật khẩu hiện tại"),
    NEWPASS_NULL(1018,"Vui lòng nhập mật khẩu mới"),
    PASS_ERROR(1019,"Mật khẩu hiện tại sai"),
    PASS_DIF(1020,"Mật khẩu phải khác mật khẩu hiện tại"),
    STATUS_LOCK(1021,"Tài khoản bạn đã bị khóa"),
    USER_ALREADY_LOCKED(1022,"Tài khoản đã khóa rồi"),
    USER_ALREADY_ACTIVE(1023,"Tài khoản này đã mở rồi"),
    TOKEN_INVALID(1024,"token khong hop le"),
    TOKEN_EXPIRED(1025,"token het han")

    ;

    private int code;
    private String messenger;
}
