package com.study.community.service;

import com.study.community.domain.Comment;
import com.study.community.domain.Member;
import com.study.community.domain.Post;
import com.study.community.dto.comment.CommentCreateRequest;
import com.study.community.dto.comment.CommentResponse;
import com.study.community.exception.BusinessException;
import com.study.community.exception.CommentNotFoundException;
import com.study.community.exception.PostNotFoundException;
import com.study.community.repository.CommentRepository;
import com.study.community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final NotificationService notificationService;

    // 댓글 작성
    @Transactional
    public CommentResponse create(Long postId, CommentCreateRequest request, Member member) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        Comment comment = new Comment(request.getContent(), member, post);
        commentRepository.save(comment);

        // 게시글 작성자에게 댓글 알림 전송
        notificationService.notifyComment(post.getMember(),member, postId);

        return new CommentResponse(comment);
    }

    // 댓글 목록 조회
    public List<CommentResponse> findByPostId(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundException(postId);
        }
        return commentRepository.findByPostId(postId).stream()
                .map(CommentResponse::new)
                .collect(Collectors.toList());
    }

    // 댓글 삭제
    @Transactional
    public void delete(Long commentId, Member member) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
        if (!comment.getMember().getId().equals(member.getId())) {
            throw new BusinessException("본인의 댓글만 삭제할 수 있습니다.");
        }
        commentRepository.delete(comment);
    }

}
