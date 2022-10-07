package com.godlife.authservice.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@DynamicUpdate
@Table(name = "USERS")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    /** 회원 번호 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    /** 회원 로그인 식별 값 */
    @Column(unique = true)
    private String identifier;

    /** 회원 로그인 타입 */
    @Column
    private String type;

    /** 회원 닉네임 */
    @Column
    private String nickname;

    /** 회원 이메일 */
    @Column
    private String email;

    /** 회원 refresh token */
    @Column
    private String refreshToken;
}
