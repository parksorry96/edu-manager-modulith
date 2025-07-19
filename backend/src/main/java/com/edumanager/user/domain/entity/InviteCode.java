package com.edumanager.user.domain.entity;

import com.edumanager.shared.domain.BaseTimeEntity;
import com.edumanager.user.domain.enums.InviteType;
import com.edumanager.shared.domain.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "invite_codes", indexes = {
        @Index(name = "idx_invite_academy", columnList = "academy_id"),
        @Index(name = "idx_invite_status", columnList = "is_used, expires_at"),
        @Index(name = "idx_invite_creator", columnList = "created_by")
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InviteCode extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true, length = 8)
    private String code;

    @Column(nullable = false)
    private Long academyId = 1L;        //추후 멀티테넨스

    @Column(nullable = false)
    private Long createdBy;

    @Enumerated(EnumType.STRING)
    private UserRole targetRole;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean isUsed = false;

    @Column
    private Long usedBy;

    @Column
    private LocalDateTime usedAt;

    @Column(length = 100)
    private String description;

    @Column(nullable = false)
    private int maxUses = 1;

    @Column(nullable = false)
    private int currentUses = 0;

    @Column
    private Long targetStudentId;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InviteType inviteType;


    public void use(Long userId) {
        if (!canBeUsed()) {
            throw new IllegalStateException("초대코드를 사용할 수 없습니다.");
        }
        this.currentUses++;
        this.usedBy = userId;
        this.usedAt = LocalDateTime.now();

        if (this.currentUses >= this.maxUses) {
            this.isUsed = true;
        }
    }

    public boolean canBeUsed() {
        return !isUsed
                && currentUses < maxUses
                && expiresAt.isAfter(LocalDateTime.now());
    }

    public boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }

    public void deactivate() {
        this.isUsed = true;
    }
}
