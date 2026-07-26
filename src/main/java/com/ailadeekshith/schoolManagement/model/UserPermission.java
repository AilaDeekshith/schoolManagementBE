package com.ailadeekshith.schoolManagement.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Per-user, per-module access grant. A user may access only the modules they
 * have a row for; {@code canWrite} enables create/update/delete while
 * {@code canRead} alone means view-only.
 */
@Entity
@Table(name = "user_permission", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "module"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(name = "module", nullable = false)
    private String module;

    @Column(name = "can_read")
    @Builder.Default
    private boolean canRead = false;

    @Column(name = "can_write")
    @Builder.Default
    private boolean canWrite = false;
}
