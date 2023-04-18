package com.sparta.daydeibackrepo.memo.dto;

import com.sparta.daydeibackrepo.memo.entity.Memo;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemoResponseDto {
    private Long id;
    private String title;
    private String content;

    public MemoResponseDto(Memo memo) {
        this.id = memo.getId();
        this.title = memo.getTitle();
        this.content = memo.getContent();
    }

}
