package cf.k5smovie.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class LoginRequestDto {
    String email;
    String password;
    boolean error = false;

    public LoginRequestDto(String email, String password, boolean error) {
        this.email = email;
        this.password = password;
        this.error = error;
    }
}
