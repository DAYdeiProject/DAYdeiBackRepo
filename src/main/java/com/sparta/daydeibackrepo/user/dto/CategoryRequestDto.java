package com.sparta.daydeibackrepo.user.dto;

import com.sparta.daydeibackrepo.user.entity.CategoryEnum;
import lombok.Getter;

import java.util.List;

@Getter
public class CategoryRequestDto {
    private List<CategoryEnum> category;

    public List<CategoryEnum> getCategory() {

        return category;
    }

    public void setCategory(List<CategoryEnum> category) {
        this.category = category;
    }
}
