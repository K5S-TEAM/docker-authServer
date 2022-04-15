package cf.k5smovie.auth.service;

import cf.k5smovie.auth.component.JwtTokenProvider;
import cf.k5smovie.auth.dto.*;
import cf.k5smovie.auth.entity.Member;
import cf.k5smovie.auth.error.ApiNotFoundException;
import cf.k5smovie.auth.error.ApiNotRespondException;
import cf.k5smovie.auth.error.LoginException;
import cf.k5smovie.auth.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    @Value("${msa.member-convenience}")
    String memberConvenienceServerUrl;

    @Transactional
    public void signUp(SignUpRequestDto dto) {
        if (this.isEmailDuplicated(dto.getEmail()) && this.isPhoneDuplicated(dto.getPhone())) {
            throw new RuntimeException("가입 정보가 올바르지 않습니다.");
        } else {
            String name = dto.getName();
            String email = dto.getEmail();
            String phone = dto.getPhone();
            String birthDate = dto.getBirthDate();
            String password = dto.getPassword();

            String regExpression = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$";

            if (!Pattern.matches(regExpression, email)) {
                throw new RuntimeException("이메일이 올바르지 않습니다.");
            }

            //현 연결된 DB에 member 정보 insert
            Member member = new Member(name, phone, email, birthDate, passwordEncoder.encode(password));
            memberRepository.save(member);

            //회원 편의기능 서버 쪽으로 nickname insert rest api 요청
            MyInformationRequestDto myInformationRequestDto = new MyInformationRequestDto(member.getId(), dto.getNickname());

            WebClient webClient = WebClient.builder().baseUrl(memberConvenienceServerUrl).build();
            webClient.post()
                    .uri("/member/nickname")
                    .body(Mono.just(myInformationRequestDto), MyInformationRequestDto.class)
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, error -> Mono.error(new ApiNotFoundException("해당 서버의 Api가 존재하지 않습니다.")))
                    .onStatus(HttpStatus::is5xxServerError, error -> Mono.error(new ApiNotRespondException("해당 서버의 응답이 없습니다.")))
                    .toBodilessEntity()
                    .block();
        }
    }

    @Transactional
    public boolean isEmailDuplicated(String email) {
        String regExpression = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[0-9a-zA-Z]{2,5}$";

        if (!Pattern.matches(regExpression, email)) {
            throw new RuntimeException("이메일이 올바르지 않습니다.");
        }

        return memberRepository.existsByEmail(email);
    }

    @Transactional
    public boolean isPhoneDuplicated(String phone) {
        String regExpression = "^[0-9]{11}$";

        if (!Pattern.matches(regExpression, phone)) {
            throw new RuntimeException("번호가 올바르지 않습니다.");
        }

        return memberRepository.existsByPhone(phone);
    }

    @Transactional
    public String login(LoginRequestDto dto) {
        Member member = memberRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new LoginException("이메일과 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(dto.getPassword(), member.getPasswordHash())) {
            throw new LoginException("이메일과 비밀번호가 올바르지 않습니다.");
        }

        return jwtTokenProvider.issueAccessToken(member.getId());
    }

    @Transactional
    public void logout(AuthenticationRequestDto dto) {
        Date expiration = jwtTokenProvider.getExpiration(dto.getAccessToken());
        redisService.setAccessToken(dto.getAccessToken(), expiration);
    }

    @Transactional
    public String findEmail(FindEmailRequestDto dto) {
        Optional<Member> result
                = memberRepository.findByPhoneAndNameAndBirthDate(dto.getPhone(), dto.getName(), dto.getBirthDate());

        if (result.isEmpty()) {
            return null;
        } else {
            return result.get().getEmail();
        }
    }

    @Transactional
    public AuthenticationResponseDto accessTokenValidation(AuthenticationRequestDto dto) {
        if (jwtTokenProvider.isTokenValid(dto.getAccessToken())) {
            return new AuthenticationResponseDto(Long.parseLong(jwtTokenProvider.getMemberId(dto.getAccessToken())));
        } else {
            return new AuthenticationResponseDto(null);
        }
    }

}
