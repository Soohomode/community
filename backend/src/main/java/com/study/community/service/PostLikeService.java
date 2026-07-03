package com.study.community.service;

import com.study.community.domain.Member;
import com.study.community.domain.Post;
import com.study.community.domain.PostLike;
import com.study.community.exception.PostNotFoundException;
import com.study.community.repository.PostLikeRepository;
import com.study.community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@Service
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final TransactionTemplate transactionTemplate;

    private static final int MAX_RETRY = 10;

    public PostLikeService(PostLikeRepository postLikeRepository,
                           PostRepository postRepository,
                           PlatformTransactionManager transactionManager) {
        this.postLikeRepository = postLikeRepository;
        this.postRepository = postRepository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.transactionTemplate.setPropagationBehavior(
                TransactionDefinition.PROPAGATION_REQUIRES_NEW); // 재시도마다 새 트랜잭션!
    }

    public boolean toggle(Long postId, Member member) {
        int retryCount = 0;

        while (true) {
            try {
                // TransactionTemplate으로 트랜잭션 직접 관리
                // 커밋까지 포함해서 try-catch로 감쌀 수 있음
                Boolean result = transactionTemplate.execute(status -> {
                    Post post = postRepository.findById(postId)
                            .orElseThrow(() -> new PostNotFoundException(postId));

                    boolean alreadyLiked = postLikeRepository.existsByMemberAndPost(member, post);

                    log.info("[toggle] postId={}, memberId={}, alreadyLiked={}, likeCount={}, version={}",
                            postId, member.getId(), alreadyLiked, post.getLikeCount(), post.getVersion());

                    if (alreadyLiked) {
                        PostLike postLike = postLikeRepository.findByMemberAndPost(member, post)
                                .orElseThrow(() -> new RuntimeException("좋아요 정보를 찾을 수 없습니다."));
                        postLikeRepository.delete(postLike);
                        post.decreaseLikeCount();
                        log.info("좋아요 취소: postId={}, memberId={}", postId, member.getId());
                        return false;
                    } else {
                        PostLike postLike = PostLike.builder()
                                .member(member)
                                .post(post)
                                .build();
                        postLikeRepository.save(postLike);
                        post.increaseLikeCount();
                        log.info("좋아요 추가: postId={}, memberId={}", postId, member.getId());
                        return true;
                    }
                });

                return Boolean.TRUE.equals(result);

            } catch (ObjectOptimisticLockingFailureException e) {
                retryCount++;
                if (retryCount >= MAX_RETRY) {
                    log.error("좋아요 처리 실패 - 최대 재시도 횟수 초과: postId={}, memberId={}",
                            postId, member.getId());
                    throw new RuntimeException("좋아요 처리에 실패했습니다. 잠시 후 다시 시도해주세요.");
                }
                log.warn("낙관적 락 충돌 - 재시도 {}/{}: postId={}, memberId={}",
                        retryCount, MAX_RETRY, postId, member.getId());
                try {
                    Thread.sleep(50L * retryCount);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("좋아요 처리 중 인터럽트 발생");
                }
            }
        }
    }
}