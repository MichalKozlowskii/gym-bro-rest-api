package com.example.gym_bro_rest_api.services.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PaginationUtils {
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_PAGE_SIZE = 20;

    public static PageRequest buildPageRequest(Integer pageNumber, Integer pageSize) {
        int queryPageNumber = (pageNumber != null && pageNumber > 0) ? pageNumber - 1 : DEFAULT_PAGE;
        int queryPageSize = (pageSize == null) ? DEFAULT_PAGE_SIZE : Math.min(pageSize, 1000);

        Sort sort = Sort.by(Sort.Order.desc("creationDate"));

        return PageRequest.of(queryPageNumber, queryPageSize, sort);
    }
}

