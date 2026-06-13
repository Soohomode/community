package com.study.community.exception;

public class MemberNotFoundException extends EntityNotFoundException {
    public MemberNotFoundException(String email) {
        super("존재하지 않는 회원입니다. email: " + email);
    }
}
