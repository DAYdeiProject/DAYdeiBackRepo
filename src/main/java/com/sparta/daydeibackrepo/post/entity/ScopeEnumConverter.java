package com.sparta.daydeibackrepo.post.entity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class ScopeEnumConverter implements AttributeConverter<ScopeEnum, String> {
    @Override
    public String convertToDatabaseColumn(ScopeEnum scopeEnum) {
        if (scopeEnum == null) {
            return null;
        }
        return scopeEnum.name();
    }

    @Override
    public ScopeEnum convertToEntityAttribute(String s) {
        if (s == null) {
            return null;
        }
        return ScopeEnum.valueOf(s);
    }
}
