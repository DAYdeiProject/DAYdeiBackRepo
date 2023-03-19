package com.sparta.daydeibackrepo.user.dto;

import org.springframework.web.multipart.MultipartFile;

public class ImageFileDto {
    private MultipartFile imageFile;

    public MultipartFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }
}
