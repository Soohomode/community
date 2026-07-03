package com.study.community.dto.post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@NoArgsConstructor
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

    // 테스트용 생성자
    public PostPageResponse(List<PostResponse> posts, int currentPage,
                            int totalPages, long totalElements, boolean hasNext) {
        this.posts = posts;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.hasNext = hasNext;
    }

}
