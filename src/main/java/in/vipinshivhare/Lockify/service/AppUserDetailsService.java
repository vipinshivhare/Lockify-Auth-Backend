package in.vipinshivhare.Lockify.service;

import java.util.ArrayList;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import in.vipinshivhare.Lockify.entity.UserEntity;
import in.vipinshivhare.Lockify.repository.UserRepostory;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AppUserDetailsService  implements UserDetailsService {

    private final UserRepostory userRepostory;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    UserEntity existingUser = userRepostory.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found for the email: "+email));
        return new User(existingUser.getEmail(), existingUser.getPassword(), new ArrayList<>());
    }
}

// UserEntity me se email leke findByEmail (Custom) jpa (repo me hai) method bnke ke email hai to email , pass send krdo
//loadUserByUsername method override kia hai ye help krta hai login API banae meh