package com.study.community.service;

import com.study.community.domain.Member;
import com.study.community.domain.Post;
import com.study.community.dto.post.PostCreateRequest;
import com.study.community.dto.post.PostResponse;
import com.study.community.exception.PostNotFoundException;
import com.study.community.repository.MemberRepository;
import com.study.community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 조회 전용 트랜잭션, 성능 최적화
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    // 게시글 작성
    @Transactional // 작성/수정/삭제는 트랜잭션 필요
    public PostResponse create(PostCreateRequest request, Member member) {
        Post post = new Post(request.getTitle(), request.getContent(), member);
        Post savedPost = postRepository.save(post);
        // 저장 후 연관관계 포함해서 다시 조회
        Post foundPost = postRepository.findById(savedPost.getId())
                .orElseThrow(() -> new PostNotFoundException(savedPost.getId()));
        return new PostResponse(foundPost);
    }

    // 게시글 전체 조회
    public List<PostResponse> findAll() {
        return postRepository.findAll().stream()
                .map(PostResponse::new)
                .collect(Collectors.toList());
    }

    // 게실글 단건 조회
    public PostResponse findById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
        return new PostResponse(post);
    }
}
