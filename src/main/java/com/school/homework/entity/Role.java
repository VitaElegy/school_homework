package com.school.homework.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Set;
import java.util.HashSet;

/**
 * 角色实体类
 * 
 * <p>表示系统中的角色，用于 RBAC（基于角色的访问控制）权限管理。</p>
 * 
 * <p>关系说明：
 * <ul>
 *   <li>多对多关系：一个角色可以拥有多个权限，一个权限可以属于多个角色</li>
 *   <li>通过 roles_permissions 中间表关联</li>
 * </ul>
 * </p>
 * 
 * <p>预定义角色：
 * <ul>
 *   <li>ROLE_ADMIN: 管理员角色，拥有所有权限</li>
 *   <li>ROLE_USER: 普通用户角色，拥有基本权限</li>
 * </ul>
 * </p>
 * 
 * <p>注意：权限使用 EAGER 加载，因为 Spring Security 需要立即访问权限信息。</p>
 * 
 * @author School Homework Team
 * @version 1.0
 */
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    
    /**
     * 角色主键ID
     * 自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 角色名称
     * 唯一标识，例如："ROLE_ADMIN", "ROLE_USER"
     * 注意：Spring Security 要求角色名以 "ROLE_" 开头
     */
    @Column(unique = true, nullable = false)
    private String name;

    /**
     * 角色拥有的权限集合
     * 多对多关系，通过 roles_permissions 中间表关联
     * 使用 EAGER 加载，因为权限信息在认证时需要立即访问
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "roles_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();
}

