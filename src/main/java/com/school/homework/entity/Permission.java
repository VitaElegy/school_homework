package com.school.homework.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 权限实体类
 * 
 * <p>表示系统中的权限，用于细粒度的访问控制。</p>
 * 
 * <p>预定义权限：
 * <ul>
 *   <li>POST_CREATE: 创建文章权限</li>
 *   <li>POST_READ: 阅读文章权限</li>
 *   <li>POST_UPDATE: 更新文章权限</li>
 *   <li>POST_DELETE: 删除文章权限</li>
 *   <li>COMMENT_CREATE: 创建评论权限</li>
 *   <li>COMMENT_DELETE: 删除评论权限</li>
 * </ul>
 * </p>
 * 
 * <p>权限分配：
 * <ul>
 *   <li>权限通过角色分配给用户</li>
 *   <li>一个用户可以有多个角色，从而拥有多个权限</li>
 * </ul>
 * </p>
 * 
 * @author School Homework Team
 * @version 1.0
 */
@Entity
@Table(name = "permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Permission {
    
    /**
     * 权限主键ID
     * 自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 权限名称
     * 唯一标识，例如："POST_CREATE", "POST_DELETE", "COMMENT_CREATE"
     * 命名规范：资源_操作（RESOURCE_ACTION）
     */
    @Column(unique = true, nullable = false)
    private String name;
}

