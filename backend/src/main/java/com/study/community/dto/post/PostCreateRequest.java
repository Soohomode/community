package com.study.community.dto.post;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게시글 작성 요청 DTO
 */
@Getter
@NoArgsConstructor
public class PostCreateRequest {

    private String title;
    private String content;

}
