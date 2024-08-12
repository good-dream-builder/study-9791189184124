package com.songko.study9791189184124.member.repository;

import com.songko.study9791189184124.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, String> {
}
