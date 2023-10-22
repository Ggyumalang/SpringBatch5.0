package com.example.batch.dto;

import com.example.batch.domain.Member;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberDTO {

    private long id;

    private String memberId;

    private String name;

    private String password;

    public void changeName(String name) {
        this.name = name;
    }

    public static MemberDTO fromEntity(Member member) {
        return MemberDTO.builder()
                .id(member.getId())
                .memberId(member.getMemberId())
                .name(member.getName())
                .password(member.getPassword())
                .build();
    }
}
