package com.example.batch.domain;

import com.example.batch.dto.MemberDTO;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String memberId;

    @Column
    private String name;

    @Column
    private String password;

    public static Member fromDto(MemberDTO memberDTO) {
        return Member.builder()
                .id(memberDTO.getId())
                .memberId(memberDTO.getMemberId())
                .name(memberDTO.getName())
                .password(memberDTO.getPassword())
                .build();
    }

}
