package com.huynh.ZaloCloneBe.exception;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // ================= AUTH =================
    USER_EXISTED(409, "USER_EXISTED", "SĐT đã tồn tại"),
    USER_NOT_FOUND(404, "USER_NOT_FOUND", "Tài khoản không tồn tại"),
    UNAUTHORIZED(401, "UNAUTHORIZED", "Thông tin đăng nhập sai"),
    ACCOUNT_LOCKED(423, "ACCOUNT_LOCKED", "Tài khoản bạn đã bị khóa"),
    USER_ALREADY_LOCKED(409, "USER_ALREADY_LOCKED", "Tài khoản đã khóa rồi"),
    USER_ALREADY_ACTIVE(409, "USER_ALREADY_ACTIVE", "Tài khoản này đã mở rồi"),

    // ================= PASSWORD =================
    PASSWORD_WRONG(400, "PASSWORD_WRONG", "Mật khẩu hiện tại sai"),
    PASSWORD_SAME_AS_OLD(400, "PASSWORD_SAME_AS_OLD", "Mật khẩu phải khác mật khẩu hiện tại"),

    // ================= FRIEND =================
    REQUEST_FRIEND_INVALID(400, "REQUEST_FRIEND_INVALID", "Không thể gửi lời mời chính mình"),
    REQUEST_NOT_FOUND(404, "REQUEST_NOT_FOUND", "Không tìm thấy lời mời"),
    REQUEST_ALREADY_ACCEPTED(400, "REQUEST_ALREADY_ACCEPTED", "Lời mời đã được chấp nhận"),
    REQUEST_CANNOT_CANCEL(400, "REQUEST_CANNOT_CANCEL", "Không thể hủy lời mời"),
    REQUEST_CANNOT_REJECT(400, "REQUEST_CANNOT_REJECT", "Không thể từ chối"),
    FRIEND_ALREADY(409, "FRIEND_ALREADY", "Đã là bạn bè"),
    FRIEND_NOT_FOUND(404, "FRIEND_NOT_FOUND", "Không tìm thấy bạn bè"),

    // ================= MESSAGE =================
    MESSAGE_NOT_FOUND(404, "MESSAGE_NOT_FOUND", "Không tìm thấy tin nhắn"),
    CONTENT_REQUIRED(400, "CONTENT_REQUIRED", "Nội dung không được để trống"),

    // ================= REQUEST =================
    REQUEST_ALREADY_SENT(400, "REQUEST_ALREADY_SENT", "Đã gửi lời mời rồi"),
    SEND_NOT_FOUND(404, "SEND_NOT_FOUND", "Không tìm thấy người gửi"),
    RECEIVE_NOT_FOUND(404, "RECEIVE_NOT_FOUND", "Không tìm thấy người nhận"),

    // ================= FILE =================
    INVALID_IMAGE_FORMAT(400, "INVALID_IMAGE_FORMAT", "Chỉ chấp nhận jpg, png"),

    // ================= CAPTCHA =================
    CAPTCHA_INVALID(400, "CAPTCHA_INVALID", "Captcha không hợp lệ"),
    CAPTCHA_REQUIRED(400, "CAPTCHA_REQUIRED", "Yêu cầu nhập captcha"),

    // ================= TOKEN =================
    TOKEN_INVALID(401, "TOKEN_INVALID", "Token không hợp lệ"),
    TOKEN_EXPIRED(401, "TOKEN_EXPIRED", "Token hết hạn"),
    TOKEN_NOT_FOUND(404, "TOKEN_NOT_FOUND", "Không tìm thấy token"),
    TOKEN_REVOKED(401, "TOKEN_REVOKED", "Token đã bị thu hồi"),

    //Search
    SEARCH_INVALID(401,"SEARCH_INVALID","TÌm kiếm không hợp lệ"),

    // ================= OTP =================
    OTP_COOLDOWN(429, "OTP_COOLDOWN", "Vui lòng gửi lại OTP sau 60 giây"),
    OTP_EXPIRED(400, "OTP_EXPIRED", "OTP đã hết hạn"),
    OTP_BLOCKED(403, "OTP_BLOCKED", "OTP đã bị khóa"),
    OTP_WRONG(400, "OTP_WRONG", "OTP không đúng"),
    OTP_NOT_FOUND(404, "OTP_NOT_FOUND", "Chưa xác minh OTP");

    private final int status;
    private final String code;
    private final String message;
}