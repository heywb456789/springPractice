package com.tomato.naraclub.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_members")
@Getter
@NoArgsConstructor
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


}
