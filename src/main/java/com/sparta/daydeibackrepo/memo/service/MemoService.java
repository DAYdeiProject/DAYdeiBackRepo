package com.sparta.daydeibackrepo.memo.service;

import com.sparta.daydeibackrepo.exception.CustomException;
import com.sparta.daydeibackrepo.memo.dto.MemoRequestDto;
import com.sparta.daydeibackrepo.memo.dto.MemoResponseDto;
import com.sparta.daydeibackrepo.memo.entity.Memo;
import com.sparta.daydeibackrepo.memo.repository.MemoRepository;
import com.sparta.daydeibackrepo.post.entity.Post;
import com.sparta.daydeibackrepo.security.UserDetailsImpl;
import com.sparta.daydeibackrepo.user.entity.User;
import com.sparta.daydeibackrepo.user.entity.UserRoleEnum;
import com.sparta.daydeibackrepo.user.repository.UserRepository;
import com.sparta.daydeibackrepo.util.StatusResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.sparta.daydeibackrepo.exception.message.ExceptionMessage.*;
import static com.sparta.daydeibackrepo.exception.message.SuccessMessage.*;

@Service
@RequiredArgsConstructor
public class MemoService {
    private final MemoRepository memoRepository;
    private final UserRepository userRepository;

    private boolean hasAuthority(User user, Memo memo) {
        return user.getId().equals(memo.getUser().getId()) || user.getRole().equals(UserRoleEnum.ADMIN);
    }

    public StatusResponseDto createMemo(MemoRequestDto requestDto, UserDetailsImpl userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(UNAUTHORIZED_MEMBER)
        );

        Memo memo = new Memo(requestDto, user);
        memoRepository.save(memo);

        return StatusResponseDto.toResponseEntity(MEMO_POST_SUCCESS);
    }

    public StatusResponseDto<?> getAllMemo(UserDetailsImpl userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(UNAUTHORIZED_MEMBER)
        );
        List<Memo> memos = memoRepository.findAllByUserOrderByCreatedAtDesc(user);
        List<MemoResponseDto> memoResponseDtos= new ArrayList<>();

        for(Memo memo : memos) {
            MemoResponseDto memoResponseDto = new MemoResponseDto(memo);
            memoResponseDtos.add(memoResponseDto);
        }

        return StatusResponseDto.toAlldataResponseEntity(memoResponseDtos);
    }

    @Transactional
    public StatusResponseDto<?> updateMemo(Long memoId, MemoRequestDto requestDto, UserDetailsImpl userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(UNAUTHORIZED_MEMBER)
        );
        Memo memo = memoRepository.findById(memoId).orElseThrow(
                () -> new CustomException(MEMO_NOT_FOUND)
        );

        if(hasAuthority(user, memo)) {
            memo.update(requestDto);
            return StatusResponseDto.toResponseEntity(MEMO_PUT_SUCCESS);
        }
        throw new CustomException(UNAUTHORIZED_UPDATE_OR_DELETE);


    }

    @Transactional
    public StatusResponseDto<?> deleteMemo(Long memoId, UserDetailsImpl userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new CustomException(UNAUTHORIZED_MEMBER)
        );
        Memo memo = memoRepository.findById(memoId).orElseThrow(
                () -> new CustomException(MEMO_NOT_FOUND)
        );
        if(hasAuthority(user, memo)) {
            memoRepository.deleteById(memoId);
            return StatusResponseDto.toResponseEntity(MEMO_DELETE_SUCCESS);
        }
        throw new CustomException(UNAUTHORIZED_UPDATE_OR_DELETE);
    }
}
