package com.edumanager.shared.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass   // 이 클래스를 상속받는 엔티티들은 이 필드들을 갖게 됨
@EntityListeners(AuditingEntityListener.class)  //JPA Auditing 기능 활성화
@SuperBuilder
@NoArgsConstructor
public abstract class BaseTimeEntity {      //추상클래스-> 단독으로 인스턴스 생성 불가능

    @CreatedDate    //엔티티 생성 시 자동으로 현재 시간 저장
    @Column(name="created_at", nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate   //엔티티 수정 시 자동으로 현재 시간 저장
    @Column(name="updated_at", nullable = false)
    private LocalDateTime updatedAt;

}
