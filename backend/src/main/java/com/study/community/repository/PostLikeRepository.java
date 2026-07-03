package com.study.community.repository;

import com.study.community.domain.Member;
import com.study.community.domain.Post;
import com.study.community.domain.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    // 특정 회원이 특정 게시글에 좋아요를 눌렀는지 확인 (true -> 취소, false -> 추가)
    boolean existsByMemberAndPost(Member member, Post post);

    // 특정 회원의 특정 게시글 좋아요 조회 (취소 시 필요)
    Optional<PostLike> findByMemberAndPost(Member member, Post post);
}