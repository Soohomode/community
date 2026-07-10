package com.study.community.service;

import com.study.community.domain.Member;
import com.study.community.dto.member.LoginRequest;
import com.study.community.dto.member.MemberJoinRequest;
import com.study.community.dto.member.TokenResponse;
import com.study.community.exception.BusinessException;
import com.study.community.exception.DuplicateEmailException;
import com.study.community.exception.MemberNotFoundException;
import com.study.community.jwt.JwtTokenProvider;
import com.study.community.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    // Email로 멤버 조회하기
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException(email));
    }

    // 회원가입
    @Transactional
    public void join(MemberJoinRequest request) {
        if (memberRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateEmailException(request.getEmail());
        }

        Member member = Member.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .build();

        memberRepository.save(member);
    }

    // 로그인
    public TokenResponse login(LoginRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new MemberNotFoundException(request.getEmail()));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new BusinessException("비밀번호가 일치하지 않습니다.");
        }

        // Access Token 생성
        String accessToken = jwtTokenProvider.generateToken(member.getEmail());

        // Refresh Token 생성 + Redis에 저장
        String refreshToken = jwtTokenProvider.generateRefreshToken(member.getEmail());
        refreshTokenService.save(
                member.getEmail(),
                refreshToken,
                jwtTokenProvider.getRefreshTokenExpirationMs()
        );

        return new TokenResponse(accessToken, refreshToken, member.getNickname());
    }

}
