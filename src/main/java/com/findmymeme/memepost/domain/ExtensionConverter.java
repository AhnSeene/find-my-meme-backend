package com.findmymeme.memepost.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ExtensionConverter implements AttributeConverter<Extension, String> {
    @Override
    public String convertToDatabaseColumn(Extension extension) {
        if (extension == null) {
            return null;
        }
        return extension.getValue();
    }

    @Override
    public Extension convertToEntityAttribute(String value) {
        if (value == null) {
            return null;
        }
        return Extension.from(value);
    }
}
