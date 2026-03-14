package com.huynh.ZaloCloneBe.until;

public class OtpUntil {
    public static String generateOtp(){
        String otp = String.valueOf((int)(Math.random() * 900000) + 100000);
        return otp;
    }
}
