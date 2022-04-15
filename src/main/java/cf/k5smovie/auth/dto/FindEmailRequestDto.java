package cf.k5smovie.auth.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class FindEmailRequestDto {
    String name = "";
    String birthDate = "";
    String phone = "";
    boolean error = false;

    public FindEmailRequestDto(String name, String birthDate, String phone, boolean error) {
        this.name = name;
        this.birthDate = birthDate;
        this.phone = phone;
        this.error = error;
    }
}
