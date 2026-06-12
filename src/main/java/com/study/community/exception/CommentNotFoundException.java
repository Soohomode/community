package com.study.community.exception;

public class CommentNotFoundException extends EntityNotFoundException {
    public CommentNotFoundException(Long id) {
        super("존재하지 않는 댓글입니다. id: " + id);
    }
}
