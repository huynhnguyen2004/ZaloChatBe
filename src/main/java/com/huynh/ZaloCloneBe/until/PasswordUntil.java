package com.huynh.ZaloCloneBe.until;

public class PasswordUntil {
    public static boolean validatePassword(String password) {
            if (password == null) return false;
            return password.matches("^[a-zA-Z0-9]{6,}$");

    }
}
