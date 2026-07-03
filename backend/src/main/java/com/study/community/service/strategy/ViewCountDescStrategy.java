package com.study.community.service.strategy;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class ViewCountDescStrategy implements PostSortStrategy { // 조회수순 정렬

    @Override
    public Sort getSort() {
        return Sort.by("viewCount").descending();
    }

    @Override
    public String getType() {
        return "popular";
    }

}