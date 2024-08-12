package com.pj.planjourney.domain.user.controller;

import com.pj.planjourney.domain.refreshtoken.service.RefreshTokenService;
import com.pj.planjourney.domain.user.dto.*;
import com.pj.planjourney.domain.user.entity.User;
import com.pj.planjourney.domain.user.repository.UserRepository;
import com.pj.planjourney.domain.user.service.UserService;
import com.pj.planjourney.global.auth.service.UserDetailsImpl;
import com.pj.planjourney.global.auth.service.UserDetailsServiceImpl;
import com.pj.planjourney.global.common.response.ApiResponse;
import com.pj.planjourney.global.common.response.ApiResponseMessage;
import com.pj.planjourney.global.jwt.filter.JwtAuthenticationFilter;
import com.pj.planjourney.global.jwt.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final RedisTemplate redisTemplate;
    private final UserRepository userRepository;

    //회원가입
    @PostMapping("")
    public ResponseEntity<ApiResponse<SignUpResponseDto>> signUp(@RequestBody SignUpRequestDto requestDto) {
        SignUpResponseDto responseDto = userService.signUp(requestDto);
        //ApiResponse<SignUpResponseDto> apiResponse = new ApiResponse<>(responseDto, ApiResponseMessage.USER_CREATED);
        ApiResponse<SignUpResponseDto> apiResponse = new ApiResponse<>(null, ApiResponseMessage.USER_CREATED);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    //카카오 로그인


    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(HttpServletRequest request) {
        String refreshToken = jwtUtil.getRefreshTokenFromHeader(request);
        if (refreshToken != null) {
            refreshTokenService.invalidateToken(refreshToken);
        }
        return  ResponseEntity.ok(new ApiResponse<>(null, ApiResponseMessage.USER_LOGOUT));
    }


    //회원탈퇴 - 요청
    @PostMapping("/signout")
    @PreAuthorize(("isAuthenticated()"))
    public ResponseEntity<?> signOut(@RequestBody SignOutRequestDto requestDto) {
        SignOutResponseDto responseDto = userService.signOut(requestDto);
        return ResponseEntity.ok(new ApiResponse<>(null, ApiResponseMessage.USER_DELETED));
    }

    //회원탈퇴 - 탈퇴
    @PostMapping("/{email}")
    public ResponseEntity<?> deactivateUser(@PathVariable String email) {
        userService.deactivateUser(email);
        return ResponseEntity.ok("삭제됨");
    }


    //회원탈퇴 - 철회
    @PostMapping("/cancel-deactivation")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> cancelDeactivation(@RequestBody DeactivateUserRequestDto requestDto) {
        userService.cancelDeactivation(requestDto.getUser().getId());
        return ResponseEntity.ok("철회됨");
    }

    //회원정보 수정
    @PatchMapping("")
    @PreAuthorize("isAuthenticated()")
    public  ResponseEntity<ApiResponse<UpdateUserResponseDto>>  updateNickname(@RequestBody UpdateUserRequestDto requestDto) {
        UpdateUserResponseDto responseDto = userService.updateNickname(requestDto);
        return ResponseEntity.ok(new ApiResponse<>(responseDto, ApiResponseMessage.USER_CHANGED));
    }

    //비밀번호 변경
    @PatchMapping("/password")
    @PreAuthorize("isAuthenticated()")
    public  ResponseEntity<ApiResponse<Void>>  updatePassword(@RequestBody UpdatePasswordRequestDto requestDto) {
        userService.updatePassword(requestDto);
        return ResponseEntity.ok(new ApiResponse<>(null, ApiResponseMessage.SUCCESS));
    }


    //마이페이지
    @GetMapping("/mypage")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<MyUserPlanListResponseDto>>> mypagePlanList(@RequestParam(required = false) Long userId) {
        List<MyUserPlanListResponseDto> responseDtoList = userService.mypagePlanList(userId);
        return ResponseEntity.ok(new ApiResponse<>(responseDtoList, ApiResponseMessage.SUCCESS));
    }


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@RequestBody LoginRequestDto loginRequest) {
        try {
            // 인증 시도
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            // 인증 후 사용자 정보 가져오기
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();

            // JWT 토큰 생성
            String accessToken = jwtUtil.createAccessToken(email, userDetails.getAuthorities());
            String refreshToken = jwtUtil.createRefreshToken(email);

            // 기존 리프레시 토큰 블랙리스트 추가
            String existingRefreshToken = (String) redisTemplate.opsForValue().get("refreshToken:" + email);
            if (existingRefreshToken != null) {
                refreshTokenService.invalidateToken(existingRefreshToken);
            }

            // 새로운 리프레시 토큰 저장
            refreshTokenService.saveRefreshToken(email, refreshToken);

            /// 사용자 정보 조회
            User user = userRepository.findByEmail(email).orElseThrow(
                    () -> new RuntimeException("User not found")
            );

            // LoginResponseDto 생성
            LoginResponseDto loginDto = new LoginResponseDto(user.getNickname(),user.getEmail());

            // 응답 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", accessToken);
            headers.add("RefreshToken", refreshToken);


            ApiResponse<LoginResponseDto> apiResponse = new ApiResponse<>(loginDto, ApiResponseMessage.USER_LOGIN);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(apiResponse);

        } catch (AuthenticationException e) {
            // 인증 예외 처리
            log.error("Authentication failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            // 일반 예외 처리
            log.error("Login error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}






