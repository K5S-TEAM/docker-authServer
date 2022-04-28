package cf.k5smovie.auth.controller;


import cf.k5smovie.auth.dto.*;
import cf.k5smovie.auth.error.ApiNotFoundException;
import cf.k5smovie.auth.error.ApiNotRespondException;
import cf.k5smovie.auth.error.LoginException;
import cf.k5smovie.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Value("${msa.member-convenience}")
    String memberConvenienceServerUrl;

    @Value("${server.domain}")
    String serverDomain;

    @GetMapping("/auth/login")
    public String loginForm(Model model) {
        model.addAttribute("serverDomain", serverDomain);
        return "auth/loginForm";
    }

    @PostMapping("/auth/login")
    public ResponseEntity login(@RequestBody LoginRequestDto dto, HttpServletResponse response){
        String accessToken = authService.login(dto);

        ResponseCookie cookie = ResponseCookie.from("accessToken", accessToken)
                .sameSite("Strict")
                .path("/")
                .build();
        response.addHeader("Set-Cookie", cookie.toString() + ";HttpOnly");

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/auth/sign-up")
    public String signUpForm(Model model){
        model.addAttribute("serverDomain", serverDomain);
        return "auth/signUpForm";
    }

    @PostMapping("/auth/sign-up")
    public ResponseEntity signUp(@RequestBody SignUpRequestDto dto){
        authService.signUp(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/auth/email-validation")
    public ResponseEntity emailValidation(@RequestBody EmailValidationRequestDto dto) {
        if (authService.isEmailDuplicated(dto.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } else {
            return ResponseEntity.status(HttpStatus.OK).build();
        }
    }

    @PostMapping("/auth/phone-validation")
    public ResponseEntity phoneValidation(@RequestBody PhoneValidationRequestDto dto) {
        if (authService.isPhoneDuplicated(dto.getPhone())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } else {
            return ResponseEntity.status(HttpStatus.OK).build();
        }
    }

    @GetMapping("/auth/find-email")
    public String findEmailForm(Model model) {
        model.addAttribute("findEmailRequestDto",new FindEmailRequestDto());
        return "auth/findEmailForm";
    }

    @PostMapping("/auth/find-email")
    public String findEmail(@ModelAttribute("findEmailRequestDto") FindEmailRequestDto dto, Model model) {
        String email = authService.findEmail(dto);

        if (email != null) {
            model.addAttribute("email", email);
            model.addAttribute("serverDomain", serverDomain);
            return "auth/findEmailResultForm";
        } else {
            model.addAttribute(new FindEmailRequestDto(dto.getName(), dto.getBirthDate(), dto.getPhone(), true));
            return "auth/findEmailForm";
        }
    }

    @ExceptionHandler
    public ResponseEntity loginExceptionHandler(LoginException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity apiNotRespondExceptionHandler(ApiNotRespondException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity apiNotFoundExceptionHandler(ApiNotFoundException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity runTimeExceptionHandler(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}
