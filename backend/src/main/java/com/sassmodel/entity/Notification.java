package com.sassmodel.entity;

import com.sassmodel.entity.base.TenantEntity;
import com.sassmodel.entity.enums.NotificationChannel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "notifications", indexes = @Index(name = "idx_notifications_user", columnList = "user_id"))
public class Notification extends TenantEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NotificationChannel channel;

    @Column(length = 200)
    private String subject;

    @Column(nullable = false, columnDefinition = "text")
    private String body;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "is_read", nullable = false)
    private boolean read = false;
}
