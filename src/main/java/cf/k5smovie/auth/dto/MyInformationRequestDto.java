package cf.k5smovie.auth.dto;

import lombok.Getter;

@Getter
public class MyInformationRequestDto {
    Long id;
    String nickname;
    String profileImageUrl;

    public MyInformationRequestDto(Long id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }
}
