package com.sparta.daydeibackrepo.post.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@NoArgsConstructor
public class MultipartListRequestDto {
    private List<MultipartFile> images;
}
