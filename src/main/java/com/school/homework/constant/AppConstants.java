package com.school.homework.constant;

/**
 * 应用常量类
 *
 * <p>定义系统中使用的常量值，包括角色名称和权限名称。</p>
 *
 * <p>使用常量类的好处：
 * <ul>
 *   <li>避免硬编码字符串，提高代码可维护性</li>
 *   <li>统一管理常量值，便于修改和扩展</li>
 *   <li>减少拼写错误的风险</li>
 * </ul>
 * </p>
 *
 * @author School Homework Team
 * @version 1.0
 */
public final class AppConstants {

    /**
     * 私有构造函数，防止实例化
     * 这是一个工具类，只包含静态常量
     */
    private AppConstants() {
        // Prevent instantiation
    }

    // ========== 角色常量 ==========

    /** 管理员角色 */
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    /** 普通用户角色 */
    public static final String ROLE_USER = "ROLE_USER";

    // ========== 权限常量 ==========

    /** 创建文章权限 */
    public static final String PERM_POST_CREATE = "POST_CREATE";

    /** 阅读文章权限 */
    public static final String PERM_POST_READ = "POST_READ";

    /** 更新文章权限 */
    public static final String PERM_POST_UPDATE = "POST_UPDATE";

    /** 删除文章权限 */
    public static final String PERM_POST_DELETE = "POST_DELETE";

    /** 创建评论权限 */
    public static final String PERM_COMMENT_CREATE = "COMMENT_CREATE";

    /** 删除评论权限 */
    public static final String PERM_COMMENT_DELETE = "COMMENT_DELETE";
}

