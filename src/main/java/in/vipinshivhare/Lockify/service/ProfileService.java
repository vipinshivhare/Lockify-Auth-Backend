package in.vipinshivhare.Lockify.service;

import in.vipinshivhare.Lockify.io.ProfileRequest;
import in.vipinshivhare.Lockify.io.ProfileResponse;

public interface ProfileService {

    ProfileResponse createProfile(ProfileRequest request);

    ProfileResponse getProfile(String email);

    void sendResetOtp(String email);

    void resetPassword(String email, String otp, String newPassword);

    void sendOtp(String email);

    void verifyOtp(String email, String otp);



}
