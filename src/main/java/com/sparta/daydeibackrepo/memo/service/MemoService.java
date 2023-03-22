package com.sparta.daydeibackrepo.memo.service;

import com.sparta.daydeibackrepo.memo.dto.MemoRequestDto;
import com.sparta.daydeibackrepo.memo.dto.MemoResponseDto;
import com.sparta.daydeibackrepo.memo.entity.Memo;
import com.sparta.daydeibackrepo.memo.repository.MemoRepository;
import com.sparta.daydeibackrepo.post.entity.Post;
import com.sparta.daydeibackrepo.security.UserDetailsImpl;
import com.sparta.daydeibackrepo.user.entity.User;
import com.sparta.daydeibackrepo.user.entity.UserRoleEnum;
import com.sparta.daydeibackrepo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemoService {
    private final MemoRepository memoRepository;
    private final UserRepository userRepository;

    private boolean hasAuthority(User user, Memo memo) {
        return user.getId().equals(memo.getUser().getId()) || user.getRole().equals(UserRoleEnum.ADMIN);
    }

    public Object createMemo(MemoRequestDto requestDto, UserDetailsImpl userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("인증된 유저가 아닙니다")
        );

        Memo memo = new Memo(requestDto, user);
        memoRepository.save(memo);

        return "메모 작성이 완료되었습니다.";
    }

    public List<MemoResponseDto> getAllMemo(UserDetailsImpl userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("인증된 유저가 아닙니다")
        );
        List<Memo> memos = memoRepository.findAllByUserOrderByCreatedAtDesc(user);
        List<MemoResponseDto> memoResponseDtos= new ArrayList<>();

        for(Memo memo : memos) {
            MemoResponseDto memoResponseDto = new MemoResponseDto(memo);
            memoResponseDtos.add(memoResponseDto);
        }

        return memoResponseDtos;
    }

    @Transactional
    public Object updateMemo(Long memoId, MemoRequestDto requestDto, UserDetailsImpl userDetails) throws IllegalAccessException {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("인증된 유저가 아닙니다")
        );
        Memo memo = memoRepository.findById(memoId).orElseThrow(
                () -> new NullPointerException("존재하지 않는 메모입니다.")
        );

        if(hasAuthority(user, memo)) {
            memo.update(requestDto);
            return "메모가 수정되었습니다.";
        }
        throw new IllegalAccessException("작성자만 삭제/수정할 수 있습니다.");


    }

    @Transactional
    public Object deleteMemo(Long memoId, UserDetailsImpl userDetails) throws IllegalAccessException {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("인증된 유저가 아닙니다")
        );
        Memo memo = memoRepository.findById(memoId).orElseThrow(
                () -> new NullPointerException("존재하지 않는 메모입니다.")
        );
        if(hasAuthority(user, memo)) {
            memoRepository.deleteById(memoId);
            return "메모가 삭제되었습니다.";
        }
        throw new IllegalAccessException("작성자만 삭제/수정할 수 있습니다.");
    }
}
