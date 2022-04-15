package cf.k5smovie.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public void setAccessToken(String accessToken, Date expiration) {
        redisTemplate.opsForValue()
                .set(accessToken, "logout", expiration.getTime() - new Date().getTime(), TimeUnit.MILLISECONDS);
    }

    public boolean isAccessTokenStored(String accessToken) {
        return redisTemplate.opsForValue()
                .get(accessToken) != null;
    }
}
