package com.tomato.naraclub.application.member.entity;

import com.tomato.naraclub.common.code.MemberStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_members")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long member_no;

    @Column(nullable = false, unique = true, length = 100)
    private String oneId;

    @Column(nullable = false, length = 20)
    private String phoneNumber;

    @Column(nullable = false, length = 10)
    private String inviteCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private MemberStatus status;


}
