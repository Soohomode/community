package com.study.community.dto.member;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberJoinRequest {
    private String email;
    private String password;
    private String nickname;
}
