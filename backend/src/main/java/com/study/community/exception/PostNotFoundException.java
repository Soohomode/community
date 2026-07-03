package com.study.community.exception;

public class PostNotFoundException extends EntityNotFoundException {
    public PostNotFoundException(Long id) {
        super("존재하지 않는 게시글입니다. id: " + id);
    }
}
