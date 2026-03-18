package com.huynh.ZaloCloneBe.until;

import com.huynh.ZaloCloneBe.entity.OtpPurpose;
import com.huynh.ZaloCloneBe.exception.AppException;
import com.huynh.ZaloCloneBe.exception.ErrorCode;
import com.huynh.ZaloCloneBe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;


public class PhoneUntil {
    public static String formatPhone(String phone){

        phone = phone.trim();

        if(phone.startsWith("0")){
            phone = "84" + phone.substring(1);
        }

        if(!phone.startsWith("84")){
            throw new AppException(ErrorCode.INVALID_PHONE);
        }

        return phone;
    }

}
