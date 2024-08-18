package com.findmymeme.response;

import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.io.Serializable;
import java.util.List;

@Getter
public class MySlice<T> implements Serializable {
    private List<T> content;
    private boolean first;
    private boolean last;
    private boolean hasNext;
    private int number;
    private int size;

    public MySlice(Slice<T> slice) {
        this.content = slice.getContent();
        this.first = slice.isFirst();
        this.last = slice.isLast();
        this.hasNext = slice.hasNext();
        this.number = slice.getNumber() + 1;
        this.size = slice.getSize();
    }
}
