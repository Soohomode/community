package com.study.community.service;

import com.study.community.domain.Member;
import com.study.community.domain.Post;
import com.study.community.dto.post.PostCreateRequest;
import com.study.community.dto.post.PostPageResponse;
import com.study.community.dto.post.PostResponse;
import com.study.community.dto.post.PostUpdateRequest;
import com.study.community.exception.BusinessException;
import com.study.community.exception.PostNotFoundException;
import com.study.community.repository.MemberRepository;
import com.study.community.repository.PostRepository;
import com.study.community.service.strategy.PostSortStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 조회 전용 트랜잭션, 성능 최적화
public class PostService {

    private final PostRepository postRepository;
    private final List<PostSortStrategy> sortStrategies;

    // 게시글 작성
    @Transactional // 작성/수정/삭제는 트랜잭션 필요
    public PostResponse create(PostCreateRequest request, Member member) {

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .member(member)
                .build();

        Post savedPost = postRepository.save(post);
        // 저장 후 연관관계 포함해서 다시 조회
        Post foundPost = postRepository.findById(savedPost.getId())
                .orElseThrow(() -> new PostNotFoundException(savedPost.getId()));
        return new PostResponse(foundPost);
    }

    // 게시글 전체 조회 (페이징 처리, Strategy)
    public PostPageResponse findAll(int page, int size, String sort) {
        PostSortStrategy strategy = sortStrategies.stream()
                .filter(s -> s.getType().equals(sort))
                .findFirst()
                .orElse(sortStrategies.stream()
                        .filter(s -> s.getType().equals("latest"))
                        .findFirst()
                        .orElseThrow());

        PageRequest pageable = PageRequest.of(page, size, strategy.getSort());
        Page<PostResponse> postPage = postRepository.findAll(pageable)
                .map(PostResponse::new);
        return new PostPageResponse(postPage);
    }

    // 게시글 단건 조회 (조회수 증가 X)
    public PostResponse findById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
        return new PostResponse(post);
    }

    // 게시글 단건 조회 (조회수 증가 O)
    @Transactional
    public PostResponse findByIdWithViewCount(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
        post.increaseViewCount(); // 조회수 증가
        return new PostResponse(post);
    }

    // 게시글 검색
    public PostPageResponse search(String keyword, Pageable pageable) {
        Page<PostResponse> page = postRepository
                .findByTitleContainingIgnoreCase(keyword, pageable)
                .map(PostResponse::new);
        return new PostPageResponse(page);
    }

    // 게시글 수정
    @Transactional
    public PostResponse update(Long id, PostUpdateRequest request, Member member) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));

        if (!post.getMember().getId().equals(member.getId())) {
            throw new BusinessException("본인의 게시글만 수정할 수 있습니다.");
        }

        post.update(request.getTitle(), request.getContent());
        return new PostResponse(post);
    }

    // 게시글 삭제
    @Transactional
    public void delete(Long id, Member member) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));

        if (!post.getMember().getId().equals(member.getId())) {
            throw new BusinessException("본인의 게시글만 삭제할 수 있습니다.");
        }

        postRepository.delete(post);
    }
}
