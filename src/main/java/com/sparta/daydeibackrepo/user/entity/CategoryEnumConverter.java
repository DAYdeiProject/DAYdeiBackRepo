package com.sparta.daydeibackrepo.user.entity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class CategoryEnumConverter implements AttributeConverter<CategoryEnum, String> {
    @Override
    public String convertToDatabaseColumn(CategoryEnum categoryEnum) {
        if (categoryEnum == null) {
            return null;
        }
        return categoryEnum.name();
    }

    @Override
    public CategoryEnum convertToEntityAttribute(String s) {
        if (s == null) {
            return null;
        }
        return CategoryEnum.valueOf(s);
    }
}