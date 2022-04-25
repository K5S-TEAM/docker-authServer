package cf.k5smovie.auth.dto;

import lombok.Getter;

@Getter
public class AuthenticationResponseDto {
    Long id;
    String name;

    public AuthenticationResponseDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
