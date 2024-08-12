package com.songko.study9791189184124.member.controller;

import com.songko.study9791189184124.member.dto.MemberDTO;
import com.songko.study9791189184124.member.security.util.JWTUtil;
import com.songko.study9791189184124.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/v1/token")
@RestController
public class TokenController {
    private final MemberService memberService;
    private final JWTUtil jwtUtil;

    @PostMapping("/make")
    public ResponseEntity<Map<String, String>> makeToken(@RequestBody MemberDTO memberDTO) {
        log.info("make token................");

        MemberDTO memberDTOResult = memberService.read(memberDTO.getMid(), memberDTO.getMpw());
        log.info(memberDTOResult);

        String mid = memberDTOResult.getMid();
        Map<String, Object> dataMap = memberDTOResult.getDataMap();
        String accessToken = jwtUtil.createToken(dataMap, 10);
        String refreshToken = jwtUtil.createToken(Map.of("mid", mid), 60 * 24 * 7);

        log.info("accessToken: {}", accessToken);
        log.info("refreshToken: {}", refreshToken);

        return ResponseEntity.ok(
                Map.of("accessToken", accessToken, "refreshToken", refreshToken)
        );
    }
}
