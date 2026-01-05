package com.school.homework.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * 用户实体类
 * 
 * <p>表示系统中的用户，包含用户的基本信息和关联关系。</p>
 * 
 * <p>关系说明：
 * <ul>
 *   <li>一个用户可以有多个文章（OneToMany）</li>
 *   <li>一个用户可以有多个评论（通过 Comment 实体关联）</li>
 *   <li>一个用户可以有多个角色（ManyToMany）</li>
 * </ul>
 * </p>
 * 
 * <p>安全说明：
 * <ul>
 *   <li>密码使用 BCrypt 加密存储，不在实体类中加密</li>
 *   <li>角色使用 EAGER 加载，因为 Spring Security 需要立即访问</li>
 * </ul>
 * </p>
 * 
 * @author School Homework Team
 * @version 1.0
 */
@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"posts", "roles"})
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {
    
    /**
     * 用户主键ID
     * 自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户名
     * 唯一标识，3-20个字符
     */
    @Column(unique = true, nullable = false)
    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    /**
     * 密码
     * 加密存储，最少6个字符
     * 注意：密码在服务层使用 BCrypt 加密后存储
     */
    @Column(nullable = false)
    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    /**
     * 邮箱地址
     * 唯一标识，需符合邮箱格式
     */
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email cannot be empty")
    private String email;

    /**
     * 头像文件名
     * 存储上传的头像文件名（UUID格式），实际文件存储在 uploads/avatars/ 目录
     */
    @Column(name = "avatar")
    private String avatar;

    /**
     * 用户创建的文章列表
     * 一对多关系，懒加载，级联删除
     */
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Post> posts;

    /**
     * 用户拥有的角色集合
     * 多对多关系，通过 users_roles 中间表关联
     * 使用 EAGER 加载，因为 Spring Security 需要立即访问角色信息
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "users_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}
