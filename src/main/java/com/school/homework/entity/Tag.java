package com.school.homework.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * 标签实体类
 *
 * <p>表示文章的标签，用于分类和检索文章。</p>
 *
 * <p>关系说明：
 * <ul>
 *   <li>多对多关系：一个标签可以关联多篇文章，一篇文章可以有多个标签</li>
 *   <li>通过 Post 实体的 tags 字段维护关系（mappedBy）</li>
 * </ul>
 * </p>
 *
 * <p>特点：
 * <ul>
 *   <li>标签名称唯一，避免重复</li>
 *   <li>标签名称不区分大小写（在业务逻辑中处理）</li>
 * </ul>
 * </p>
 *
 * @author School Homework Team
 * @version 1.0
 */
@Entity
@Table(name = "tags")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"posts"})
@NoArgsConstructor
public class Tag extends BaseEntity {

    /**
     * 标签主键ID
     * 自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 标签名称
     * 唯一标识，必填
     * 例如："Java", "Spring Boot", "教程"
     */
    @Column(unique = true, nullable = false)
    @NotBlank(message = "Tag name cannot be empty")
    private String name;

    /**
     * 使用该标签的文章集合
     * 多对多关系的反向端，由 Post 实体维护关系
     */
    @ManyToMany(mappedBy = "tags")
    private Set<Post> posts = new HashSet<>();

    /**
     * 带参数的构造函数
     * 用于快速创建标签对象
     *
     * @param name 标签名称
     */
    public Tag(String name) {
        this.name = name;
    }
}


