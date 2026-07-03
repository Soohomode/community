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

    // 게시글 목록 조회 시 작성자(Member)를 함께 조회 (N+1 방지)
    @Query(
            value = "SELECT p FROM Post p JOIN FETCH p.member",
            countQuery = "SELECT count(p) FROM Post p" // count 쿼리는 fetch join 불필요 (성능 최적화)
    )
    Page<Post> findAllWithMember(Pageable pageable);

    // 게시글 제목으로 검색 (작성자 함께 조회, N+1 방지)
    @Query(
            value = "SELECT p FROM Post p JOIN FETCH p.member WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))",
            countQuery = "SELECT count(p) FROM Post p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))"
    )
    Page<Post> findByTitleContainingIgnoreCaseWithMember(@Param("keyword") String keyword, Pageable pageable);
}
