package cf.k5smovie.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequestDto {
    String name;
    String birthDate;
    String phone;
    String nickname;
    String email;
    String password;
}
