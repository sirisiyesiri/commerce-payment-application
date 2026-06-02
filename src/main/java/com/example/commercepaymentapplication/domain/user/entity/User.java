package com.example.commercepaymentapplication.domain.user.entity;

import com.example.commercepaymentapplication.domain.membership.MembershipGrade;
import com.example.commercepaymentapplication.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 60)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(nullable = false)
    private Integer pointBalance = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MembershipGrade membershipGrade = MembershipGrade.NORMAL;

    @Column(nullable = false)
    private Integer totalPaidAmount = 0;

    @Column
    private LocalDateTime gradeChangedAt;

    @Builder
    public User(String email, String password, String name, String phoneNumber) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.pointBalance = 0;
        this.membershipGrade = MembershipGrade.NORMAL;
        this.totalPaidAmount = 0;
    }
}
