package com.example._faEmail.service;

import com.example._faEmail.dto.LoginUserDto;
import com.example._faEmail.dto.RegisterUserDto;
import com.example._faEmail.dto.VerifyCodeDto;
import com.example._faEmail.model.UserModel;
import com.example._faEmail.repository.UserRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.Map;

@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository repo;
    private final JwtService jwtService;
    private final TwoFactorAuthService twoFactorAuthService;
    private final RedisTemplate redisTemplate;
    public UserService(
            PasswordEncoder passwordEncoder,
            UserRepository repo,
            JwtService jwtService,
            TwoFactorAuthService twoFactorAuthService,
            RedisTemplate redisTemplate) {
        this.passwordEncoder = passwordEncoder;
        this.repo = repo;
        this.jwtService = jwtService;
        this.twoFactorAuthService = twoFactorAuthService;
        this.redisTemplate = redisTemplate;
    }

    public UserModel register(RegisterUserDto request){
        if (!request.password().equals(request.repeatedPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match");
        }
        if (repo.existsByNickname(request.nickname())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This nickname is already in use");
        }

        if (repo.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This email is already in use");
        }

        String hashedPass = passwordEncoder.encode(request.password());
        UserModel userModel = UserModel.fromDto(request, hashedPass);

        userModel.setVerified(false);
        UserModel savedUser = repo.save(userModel);
        String code = twoFactorAuthService.generate2FaCode();
        String redisKey = "2fa:" + savedUser.getNickname();
        redisTemplate.opsForValue().set(redisKey, code, Duration.ofMinutes(5));

        twoFactorAuthService.sendVerificationEmail(request.email(), code);
        return savedUser;
    }

    public ResponseEntity<?> login(LoginUserDto request) {
        UserModel user = repo.findByNickname(request.nickname())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(request.password(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "INVALID_CREDENTIALS"));
        }

        if (!user.isVerified()) {
            String redisKey = "2fa:" + user.getNickname();

            if (Boolean.FALSE.equals(redisTemplate.hasKey(redisKey))) {
                String code = twoFactorAuthService.generate2FaCode();
                redisTemplate.opsForValue().set(redisKey, code, Duration.ofMinutes(5));
                twoFactorAuthService.sendVerificationEmail(user.getEmail(), code);
            }

            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "UNVERIFIED_EMAIL"));
        }

        String token = jwtService.generateToken(user);

        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(24 * 60 * 60)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("message", "Login successful"));
    }

    public ResponseEntity<?> verifyCode(VerifyCodeDto request) {
        String redisKey = "2fa:" + request.nickname();
        String savedCode = (String) redisTemplate.opsForValue().get(redisKey);

        if (savedCode == null || !savedCode.equals(request.code())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "INVALID_OR_EXPIRED_CODE"));
        }

        UserModel user = repo.findByNickname(request.nickname())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setVerified(true);
        repo.save(user);

        redisTemplate.delete(redisKey);

        String token = jwtService.generateToken(user);

        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(24 * 60 * 60)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("message", "Verification successful, logged in"));
    }
}
