package com.school.homework.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 评论实体类
 * 
 * <p>表示用户对文章的评论，包含评论内容和关联关系。</p>
 * 
 * <p>关系说明：
 * <ul>
 *   <li>多对一关系：每个评论属于一篇文章（Post）</li>
 *   <li>多对一关系：每个评论属于一个用户（User）</li>
 * </ul>
 * </p>
 * 
 * @author School Homework Team
 * @version 1.0
 */
@Entity
@Table(name = "comments")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends BaseEntity {
    
    /**
     * 评论主键ID
     * 自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 评论内容
     * 使用 TEXT 类型存储，支持长文本
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    /**
     * 所属文章
     * 多对一关系，懒加载
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    /**
     * 评论作者
     * 多对一关系，懒加载
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User author;
}
