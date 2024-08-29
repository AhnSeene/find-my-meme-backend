package com.findmymeme.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.List;

@Getter
public class MyPage<T> implements Serializable {

    private List<T> content;
    private boolean first;
    private boolean last;
    private boolean hasNext;
    private int totalPages;
    private long totalElements;
    private int page;
    private int size;

    public MyPage(Page<T> page) {
        this.content = page.getContent();
        this.first = page.isFirst();
        this.last = page.isLast();
        this.hasNext = page.hasNext();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.page = page.getNumber() + 1;
        this.size = page.getSize();
    }
}
