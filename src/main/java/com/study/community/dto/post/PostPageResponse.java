package com.study.community.dto.post;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class PostPageResponse {

    private List<PostResponse> posts;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private boolean hasNext; // true or false

    public PostPageResponse(Page<PostResponse> page) {
        this.posts = page.getContent();
        this.currentPage = page.getNumber();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.hasNext = page.hasNext();
    }

}
