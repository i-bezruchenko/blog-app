package ru.yandex.practicum.blog_app.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Paging {

    private int pageSize;
    private int pageNumber;
    private int pageCount;

    public Paging(int postCount, int pageNumber, int pageSize) {
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
        this.pageCount = postCount / pageSize + (postCount % pageSize > 0 ? 1 : 0);
    }

    public boolean hasNext() {
        return pageNumber < pageCount;
    }

    public boolean hasPrevious() {
        return pageNumber > 1;
    }
}
