package com.songko.study9791189184124.member.exception;

import lombok.Getter;

/**
 * 회원과 관련된 모든 예외
 */
@Getter
public class MemberTaskException extends RuntimeException {
    private String msg;
    private int code;

    public MemberTaskException(String msg, int code) {
        this.msg = msg;
        this.code = code;
    }
}
