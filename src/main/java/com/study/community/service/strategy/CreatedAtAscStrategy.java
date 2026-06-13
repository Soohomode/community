package com.study.community.service.strategy;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class CreatedAtAscStrategy implements PostSortStrategy { // 오래된순 정렬

    @Override
    public Sort getSort() {
        return Sort.by("createdAt").ascending();
    }

    @Override
    public String getType() {
        return "oldest";
    }

}
