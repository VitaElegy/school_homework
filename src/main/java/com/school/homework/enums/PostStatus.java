package com.school.homework.enums;

/**
 * 文章状态枚举
 *
 * <p>定义文章的三种状态：</p>
 * <ul>
 *   <li>DRAFT: 草稿状态，文章未发布，仅作者可见</li>
 *   <li>PUBLISHED: 已发布状态，文章已发布，所有用户可见</li>
 *   <li>ARCHIVED: 已归档状态，文章已归档，通常不再显示在列表中</li>
 * </ul>
 *
 * @author School Homework Team
 * @version 1.0
 */
public enum PostStatus {
    /** 草稿状态 */
    DRAFT,
    /** 已发布状态 */
    PUBLISHED,
    /** 已归档状态 */
    ARCHIVED
}


