package com.school.homework.entity;

import com.school.homework.enums.PostStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 文章实体类
 *
 * <p>表示博客系统中的文章，包含文章内容、状态、标签等信息。</p>
 *
 * <p>关系说明：
 * <ul>
 *   <li>多对一关系：每篇文章属于一个作者（User）</li>
 *   <li>一对多关系：每篇文章可以有多个评论（Comment）</li>
 *   <li>多对多关系：每篇文章可以有多个标签（Tag）</li>
 * </ul>
 * </p>
 *
 * <p>内容格式：
 * <ul>
 *   <li>content 字段存储 Markdown 格式的原始内容</li>
 *   <li>显示时通过 MarkdownService 渲染为 HTML</li>
 * </ul>
 * </p>
 *
 * @author School Homework Team
 * @version 1.0
 */
@Entity
@Table(name = "posts")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"tags", "comments", "author"})
@NoArgsConstructor
@AllArgsConstructor
public class Post extends BaseEntity {

    /**
     * 文章主键ID
     * 自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 文章标题
     * 必填，最大100个字符
     */
    @Column(nullable = false)
    @NotBlank(message = "Title cannot be empty")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;

    /**
     * 文章内容
     * Markdown 格式的文本内容，使用 TEXT 类型存储
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Content cannot be empty")
    private String content;

    /**
     * 文章状态
     * DRAFT: 草稿
     * PUBLISHED: 已发布
     * ARCHIVED: 已归档
     * 默认值为 DRAFT
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Status cannot be null")
    private PostStatus status = PostStatus.DRAFT;

    /**
     * 浏览次数
     * 记录文章被查看的次数，默认值为0
     * 使用原子操作递增，避免并发问题
     */
    @Column(columnDefinition = "integer default 0")
    private int viewCount = 0;

    /**
     * 文章作者
     * 多对一关系，懒加载
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User author;

    /**
     * 文章评论列表
     * 一对多关系，懒加载，级联删除
     */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments;

    /**
     * 文章标签集合
     * 多对多关系，通过 post_tags 中间表关联
     * 使用 PERSIST 和 MERGE 级联，允许新建和更新标签
     */
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "post_tags",
        joinColumns = @JoinColumn(name = "post_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    /**
     * 添加标签
     * 同时维护双向关系，确保数据一致性
     *
     * @param tag 要添加的标签
     */
    public void addTag(Tag tag) {
        this.tags.add(tag);
        tag.getPosts().add(this);
    }

    /**
     * 移除标签
     * 同时维护双向关系，确保数据一致性
     *
     * @param tag 要移除的标签
     */
    public void removeTag(Tag tag) {
        this.tags.remove(tag);
        tag.getPosts().remove(this);
    }
}
