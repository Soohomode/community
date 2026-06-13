package com.study.community.service.strategy;

import org.springframework.data.domain.Sort;

public interface PostSortStrategy {
    Sort getSort();
    String getType();
}
