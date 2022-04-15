package cf.k5smovie.auth.dto;

import lombok.Getter;

@Getter
public class AuthenticationResponseDto {
    Long id;

    public AuthenticationResponseDto(Long id) {
        this.id = id;
    }
}
