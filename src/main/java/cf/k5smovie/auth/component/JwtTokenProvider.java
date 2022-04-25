package cf.k5smovie.auth.component;

import cf.k5smovie.auth.dto.AuthenticationResponseDto;
import cf.k5smovie.auth.service.RedisService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    private Key key;
    private static Long ACCESS_TOKEN_VALID_TIME = 1000L * 60 * 60 * 6;
//    private static Long REFRESH_TOKEN_VALID_TIME = 1000L * 60 * 60 * 24 * 14;

    private final UserDetailsService userDetailsService;
    private final RedisService redisService;

    @PostConstruct
    protected void init(){
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String issueAccessToken(Long id, String name) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(id));

        List<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");
        claims.put("roles", roles);
        claims.put("name", name);

        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_VALID_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

//    public String issueRefreshToken() {
//        Date now = new Date();
//
//        return Jwts.builder()
//                .setIssuedAt(now)
//                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_VALID_TIME))
//                .signWith(key, SignatureAlgorithm.HS256)
//                .compact();
//    }

    public boolean isTokenValid(String accessToken) {
        if (redisService.isAccessTokenStored(accessToken)) {
            return false;
        }

        try {
            Jws<Claims> claims = Jwts
                    .parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken);

            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException e) {
            return false;
        }
    }

    public Authentication getAuthentication(String accessToken) {
        String id = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(accessToken)
                .getBody().getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(id);

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getMemberId(String accessToken) {
        return Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken)
                .getBody()
                .getSubject();
    }

    public Date getExpiration(String accessToken) {
        return Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken)
                .getBody()
                .getExpiration();
    }

    public AuthenticationResponseDto getMemberInfo(String accessToken) {
        Claims payload = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(accessToken).getBody();

        return new AuthenticationResponseDto(Long.parseLong(payload.getSubject()), payload.get("name", String.class));
    }
}
