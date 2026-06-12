package com.study.community.service;

import com.study.community.domain.Member;
import com.study.community.dto.member.LoginRequest;
import com.study.community.dto.member.MemberJoinRequest;
import com.study.community.dto.member.TokenResponse;
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

        Member member = new Member(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getNickname()
        );

        memberRepository.save(member);
    }

    // 로그인
    public TokenResponse login(LoginRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new MemberNotFoundException(request.getEmail()));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String token = jwtTokenProvider.generateToken(member.getEmail());
        return new TokenResponse(token);
    }

}
