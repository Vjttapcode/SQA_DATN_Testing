package com.ptit.schedule.dto;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public class PageResponse<T> {
    private final List<T> content;
    private final int pageNum;
    private final int pageSize;
    private final long total;

    public PageResponse(List<T> content, int pageNum, int pageSize, long total) {
        this.content = content == null ? null : Arrays.asList((T[]) content.toArray());
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private List<T> content;
        private int pageNum;
        private int pageSize;
        private long total;

        public Builder<T> content(List<T> content) {
            this.content = content;
            return this;
        }

        public Builder<T> pageNum(int pageNum) {
            this.pageNum = pageNum;
            return this;
        }

        public Builder<T> pageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public Builder<T> total(long total) {
            this.total = total;
            return this;
        }

        public PageResponse<T> build() {
            return new PageResponse<>(content, pageNum, pageSize, total);
        }
    }
}
