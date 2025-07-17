package in.vipinshivhare.Lockify.io;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileRequest {


    @NotBlank(message = "Name should be not empty")
    private String name;
    @Email(message = "Enter valid email address")
    @NotBlank(message = "Email should be not empty")
    private String email;
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;


}
