package cf.k5smovie.auth.controller;


import cf.k5smovie.auth.dto.AuthenticationRequestDto;
import cf.k5smovie.auth.dto.AuthenticationResponseDto;
import cf.k5smovie.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthApiController {

    private final AuthService authService;

    @PostMapping("/auth/access-token-valid")
    public ResponseEntity accessTokenValidation(@RequestBody AuthenticationRequestDto dto) {
        log.info("request token: "+ dto.getAccessToken());
        AuthenticationResponseDto authenticationResponseDto = authService.accessTokenValidation(dto);

        if (authenticationResponseDto.getId() == null) {
            log.info("request token("+ dto.getAccessToken()+") result: invalid");
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(authenticationResponseDto);
        } else {
            log.info("request token("+ dto.getAccessToken()+") result: valid");
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(authenticationResponseDto);
        }
    }

    @PostMapping("/auth/logout")
    public ResponseEntity logout(@RequestBody AuthenticationRequestDto dto) {
        log.info("logout token: " + dto.getAccessToken());
        authService.logout(dto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}
