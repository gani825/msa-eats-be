package com.green.eats.auth.entity;

import com.green.eats.common.entity.CreatedUpdatedAt;
import com.green.eats.common.model.EnumUserRole;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity // DB 테이블과 매핑되는 JPA 엔티티
@Getter
@Setter
@NoArgsConstructor // JPA는 기본 생성자가 필수
public class User extends CreatedUpdatedAt { // created_at, updated_at 자동 관리 상속

    @Id
    @Tsid // Twitter Snowflake 기반 ID 자동 생성 (Long 타입, 시간순 정렬 가능)
    private Long id;

    @Column(unique = true, nullable = false) // 이메일은 중복 불가, NULL 불가
    private String email;

    @Column(nullable = false) // 비밀번호는 NULL 불가
    private String password;

    @Column(nullable = false) // 이름은 NULL 불가
    private String name;

    private String address; // 주소는 선택값 (NULL 허용)


    // DB에 코드값("01", "02", "03")으로 저장, EnumUserRole.CodeConverter가 자동 변환
    @Column(nullable = false, length = 2)
    private EnumUserRole enumUserRole;
}