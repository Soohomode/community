package com.study.community.service;

import com.study.community.domain.Member;
import com.study.community.domain.Post;
import com.study.community.domain.PostLike;
import com.study.community.exception.PostNotFoundException;
import com.study.community.repository.PostLikeRepository;
import com.study.community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;

    @Transactional
    public boolean toggle(Long postId, Member member) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        boolean alreadyLiked = postLikeRepository.existsByMemberAndPost(member, post);

        if (alreadyLiked) {
            // 좋아요 취소
            PostLike postLike = postLikeRepository.findByMemberAndPost(member, post)
                    .orElseThrow(() -> new RuntimeException("좋아요 정보를 찾을 수 없습니다."));
            postLikeRepository.delete(postLike);
            post.decreaseLikeCount();
            log.info("좋아요 취소: postId={}, memberId={}", postId, member.getId());
            return false; // 취소됨
        } else {
            // 좋아요 추가
            PostLike postLike = PostLike.builder()
                    .member(member)
                    .post(post)
                    .build();
            postLikeRepository.save(postLike);
            post.increaseLikeCount();
            log.info("좋아요 추가: postId={}, memberId={}", postId, member.getId());
            return true; // 추가됨
        }
    }
}