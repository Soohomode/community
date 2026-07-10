package com.study.community.service;

import com.study.community.domain.Member;
import com.study.community.dto.member.LoginRequest;
import com.study.community.dto.member.MemberJoinRequest;
import com.study.community.dto.member.TokenResponse;
import com.study.community.exception.BusinessException;
import com.study.community.exception.MemberNotFoundException;
import com.study.community.jwt.JwtTokenProvider;
import com.study.community.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.ArgumentMatchers.anyLong;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private MemberService memberService;

    private Member testMember;

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .email("test@test.com")
                .password("encodedPassword")
                .nickname("테스터")
                .build();
    }

    @Test
    @DisplayName("회원가입 성공")
    void join_success() {
        // given
        MemberJoinRequest request = new MemberJoinRequest();
        given(memberRepository.findByEmail(any())).willReturn(Optional.empty());
        given(passwordEncoder.encode(any())).willReturn("encodedPassword");
        given(memberRepository.save(any(Member.class))).willReturn(testMember);

        // when & then
        assertThatNoException().isThrownBy(() -> memberService.join(request));
        then(memberRepository).should().save(any(Member.class));
    }

    @Test
    @DisplayName("중복 이메일로 회원가입 시 예외 발생")
    void join_duplicateEmail() {
        // given
        MemberJoinRequest request = new MemberJoinRequest();
        given(memberRepository.findByEmail(any())).willReturn(Optional.of(testMember));

        // when & then
        assertThatThrownBy(() -> memberService.join(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("이미 사용 중인 이메일");
    }

    @Test
    @DisplayName("로그인 성공 시 토큰 반환")
    void login_success() {
        // given
        LoginRequest request = new LoginRequest();
        given(memberRepository.findByEmail(any())).willReturn(Optional.of(testMember));
        given(passwordEncoder.matches(any(), any())).willReturn(true);
        given(jwtTokenProvider.generateToken(any())).willReturn("access-token");
        given(jwtTokenProvider.generateRefreshToken(any())).willReturn("refresh-token");
        given(jwtTokenProvider.getRefreshTokenExpirationMs()).willReturn(604800000L);
        willDoNothing().given(refreshTokenService).save(any(), any(), anyLong());

        // when
        TokenResponse response = memberService.login(request);

        // then
        assertThat(response.getToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getNickname()).isEqualTo("테스터");
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 로그인 시 예외 발생")
    void login_memberNotFound() {
        // given
        LoginRequest request = new LoginRequest();
        given(memberRepository.findByEmail(any())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.login(request))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @DisplayName("비밀번호 불일치 시 예외 발생")
    void login_wrongPassword() {
        // given
        LoginRequest request = new LoginRequest();
        given(memberRepository.findByEmail(any())).willReturn(Optional.of(testMember));
        given(passwordEncoder.matches(any(), any())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> memberService.login(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("비밀번호가 일치하지 않습니다");
    }
}