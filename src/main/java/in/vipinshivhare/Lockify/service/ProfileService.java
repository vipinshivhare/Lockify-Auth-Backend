package in.vipinshivhare.Lockify.service;

import in.vipinshivhare.Lockify.io.ProfileRequest;
import in.vipinshivhare.Lockify.io.ProfileResponse;

public interface ProfileService {

ProfileResponse createProfile(ProfileRequest request);

    ProfileResponse getProfile(String email);



}
