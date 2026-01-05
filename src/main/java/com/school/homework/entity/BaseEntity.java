package com.school.homework.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 基础实体类
 *
 * <p>所有实体类的基类，提供审计字段（创建时间和更新时间）。
 * 使用 JPA 审计功能自动填充这些字段。</p>
 *
 * <p>子类需要继承此类以自动获得审计功能：
 * <ul>
 *   <li>createdAt: 记录实体创建时间，创建后不可修改</li>
 *   <li>updatedAt: 记录实体最后更新时间，每次更新时自动更新</li>
 * </ul>
 * </p>
 *
 * <p>注意：需要在 JpaConfig 中启用 JPA 审计功能才能正常工作。</p>
 *
 * @author School Homework Team
 * @version 1.0
 */
@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    /**
     * 创建时间
     * 实体创建时自动设置，之后不可修改
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     * 实体每次更新时自动更新
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

