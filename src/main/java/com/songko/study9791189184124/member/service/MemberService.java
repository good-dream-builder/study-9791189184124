package com.songko.study9791189184124.member.service;


import com.songko.study9791189184124.member.dto.MemberDTO;
import com.songko.study9791189184124.member.entity.MemberEntity;
import com.songko.study9791189184124.member.exception.MemberExceptions;
import com.songko.study9791189184124.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberDTO read(String mid, String mpw) {
        // 사용자의 정보를 가져옴.
        Optional<MemberEntity> result = memberRepository.findById(mid);
        // 보안을 위해 NOT_FOUND가 아닌, BAD_CREDENTIALS로 전달
        MemberEntity memberEntity = result.orElseThrow(MemberExceptions.BAD_CREDENTIALS::get);

        // 비밀번호 확인
        if (!passwordEncoder.matches(mpw, memberEntity.getMpw())) {
            throw MemberExceptions.BAD_CREDENTIALS.get();
        }
        return new MemberDTO(memberEntity);
    }
}
