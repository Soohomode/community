package com.study.community.repository;

import com.study.community.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {
    /**
     * JpaRepository 를 상속 받으면 아래 메서드가 자동 생성
     * save()        ← 저장
     * findById()    ← ID로 조회
     * findAll()     ← 전체 조회
     * delete()      ← 삭제
     */

    // 페이징 전체 조회
    Page<Post> findAll(Pageable pageable);

    // 게시글 제목으로 검색
    Page<Post> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + :additionalViews WHERE p.id = :postId")
    void addViewCount(@Param("postId") Long postId, @Param("additionalViews") long additionalViews);
}
