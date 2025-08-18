package com.cafe.management.serviceImpl;

import java.util.Random;

public class GenerateOtp {
    public String generateOtp(int input){
        String alphabetsSmall ="abcdefghijklmnopqrstuvwxyz";
        String alphabetsCapital = alphabetsSmall.toUpperCase();
        String digits = "abcdefghijklmnopqrstuvwxyz1234567890"+alphabetsCapital;
        StringBuilder otp = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < input; i++) {
            otp.append(digits.charAt(random.nextInt(digits.length())));
        }
        return otp.toString();
    }

    public static void main(String[] args) {
        GenerateOtp otp = new GenerateOtp();
        System.out.println(otp.generateOtp(6));
    }
}
