package com.sparta.daydeibackrepo.post.entity;


import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class ColorEnumConverter implements AttributeConverter<ColorEnum, String> {
    @Override
    public String convertToDatabaseColumn(ColorEnum colorEnum) {
        if (colorEnum == null) {
            return null;
        }
        return colorEnum.name();
    }

    @Override
    public ColorEnum convertToEntityAttribute(String s) {
        if (s == null) {
            return null;
        }
        return ColorEnum.valueOf(s);
    }
}
