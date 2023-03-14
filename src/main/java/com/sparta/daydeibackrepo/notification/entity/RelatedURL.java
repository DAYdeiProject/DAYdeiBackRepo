package com.sparta.daydeibackrepo.notification.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RelatedURL {

    private static final int MAX_LENGTH = 255;

    @Column(nullable = false, length = MAX_LENGTH)
    private String url;

    public RelatedURL(String url) {
        if (isNotValidRelatedURL(url)) {
            throw new IllegalArgumentException(); //InvalidRelatedURLException
        }
        this.url = url;
    }

    private boolean isNotValidRelatedURL(String url) {
        return Objects.isNull(url) || url.isBlank() ||
                url.length() > MAX_LENGTH;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RelatedURL)) return false;
        RelatedURL that = (RelatedURL) o;
        return getUrl().equals(that.getUrl());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUrl());
    }
}
