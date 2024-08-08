package com.pj.planjourney.domain.user.service;

import com.pj.planjourney.domain.blacklist.entity.BlackList;
import com.pj.planjourney.domain.blacklist.repository.BlackListRepository;
import com.pj.planjourney.domain.blacklist.service.BlackListService;
import com.pj.planjourney.domain.refreshtoken.service.RefreshTokenService;
import com.pj.planjourney.domain.user.dto.*;
import com.pj.planjourney.domain.user.entity.Role;
import com.pj.planjourney.domain.user.entity.User;
import com.pj.planjourney.domain.user.repository.UserRepository;
import com.pj.planjourney.global.auth.service.UserDetailsImpl;
import com.pj.planjourney.global.jwt.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final BlackListRepository blackListRepository;
    private final BlackListService blackListService;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;

    //회원가입
    public SignUpResponseDto signUp(SignUpRequestDto requestDto) {
        //이메일 본인인증해야함
        String email = requestDto.getEmail();
        String password = passwordEncoder.encode(requestDto.getPassword());
        String nickname = requestDto.getNickname();
        Role role = requestDto.getRole();

        Optional<User> checkEmail = userRepository.findByEmail(email);
        if (checkEmail.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        Optional<User> checkNickname = userRepository.findByNickname(nickname);
        if (checkNickname.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        User user = new User(email, password, nickname, role);
        User savedUser = userRepository.save(user);
        return new SignUpResponseDto(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getNickname(),
                savedUser.getRole().getAuthority());
    }


    //회원정보 수정(닉네임)
    public UpdateUserResponseDto updateNickname(UpdateUserRequestDto requestDto) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        log.info(email);

        User user = userRepository.findByEmail(email).orElseThrow(()->
                new RuntimeException("user 없음"));

        if(!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("비번 틀림");
        }

        user.updateNickname(requestDto.getNickname());
        userRepository.save(user);

        return new UpdateUserResponseDto(user.getNickname());
    }

    //회원 탈퇴 - 요청
    public SignOutResponseDto signOut(SignOutRequestDto requestDto){
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        String password = userDetails.getPassword();
        LocalDateTime deletedAt = LocalDateTime.now();
//        LocalDateTime validAt = LocalDateTime.now().plusDays(10);
        LocalDateTime validAt = LocalDateTime.now().plusMinutes(2); //임시로 2분 넣어놓고 확인하기

        User user = userRepository.findByEmail(email).orElseThrow(()->
                new RuntimeException("user 없음"));


        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("비번 틀림");
        }

        BlackList blackList = new BlackList(user,deletedAt, validAt);
        BlackList savedBlackList = blackListRepository.save(blackList);

        return new SignOutResponseDto(
                savedBlackList.getId(),
                savedBlackList.getUser().getId(),
                savedBlackList.getDeletedAt(),
                savedBlackList.getValidAt());
    }

    //유저 값 받기
    public User getUser(String token) {
        String userEmail = jwtUtil.getUserInfoFromToken(token).getSubject(); // 토큰에서 사용자 ID 추출
        User user = userRepository.findByEmail(userEmail).orElseThrow(() ->
                new RuntimeException("User not found"));
        log.info("Extracted email from token: {}", userEmail);
        log.info("Found user: {}", user);
        return user;
    }

    // 회원 탈퇴 - 탈퇴
    public DeactivateUserResponseDto deactivateUser(String email) {
        log.info("Deactivating user with email: {}", email);
        User user = userRepository.findByEmail(email).orElseThrow();
        String annoymousEmail = "annoymous" + user.getId() + "@email.com";
        String annoymousNickname = "Annoymous" + user.getId();
        String annoymousPassword = "annoymous";

        user.deactivateUser(annoymousEmail, annoymousNickname, annoymousPassword);
        userRepository.save(user);
//        try {
//            blackListService.deleteUser(user.getId());
//            log.info("User with email {} deactivated and removed from blacklist", user.getId());
//        } catch (Exception e) {
//            log.error("Error removing user from blacklist: {}", e.getMessage());
//        }

        // 공통응답포맷 적용해야함
        return new DeactivateUserResponseDto(user.getId(), user.getEmail(), user.getNickname(), user.getPassword());
    }

    //회원 탈퇴 - 철회
    public void cancelDeactivation(Long userId){
        Optional<BlackList> checkedBlackList = blackListRepository.findByUserId(userId);
        if(!checkedBlackList.isPresent()){
//            throw new IllegalArgumentException("없음.");
            return; // ??? 블랙리스트에 없으면 철회안해도되는건가요? 넹
        }
        // 삭제 확인
        Optional<BlackList> deletedEntry = blackListRepository.findByUserId(userId);

        if (deletedEntry.isPresent()) {
            throw new IllegalStateException("사용자 삭제 실패");
        }
    }



    //비밀번호 변경

    //마이페이지 ?

}
