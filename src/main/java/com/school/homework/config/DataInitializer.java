package com.school.homework.config;

import com.school.homework.constant.AppConstants;
import com.school.homework.dao.PermissionRepository;
import com.school.homework.dao.RoleRepository;
import com.school.homework.dao.UserRepository;
import com.school.homework.entity.Permission;
import com.school.homework.entity.Role;
import com.school.homework.entity.User;
import com.school.homework.service.PostImportService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据初始化配置类
 *
 * <p>在应用启动时自动执行数据初始化，包括：
 * <ul>
 *   <li>创建默认权限（POST_CREATE, POST_READ, POST_UPDATE, POST_DELETE, COMMENT_CREATE, COMMENT_DELETE）</li>
 *   <li>创建默认角色（ROLE_ADMIN, ROLE_USER）并分配权限</li>
 *   <li>创建默认用户（管理员和普通用户）</li>
 *   <li>从 Markdown 文件自动导入文章</li>
 * </ul>
 * </p>
 *
 * <p>配置说明：
 * <ul>
 *   <li>默认用户信息在 application.properties 中配置</li>
 *   <li>如果用户已存在，则跳过创建（避免重复创建）</li>
 * </ul>
 * </p>
 *
 * @author School Homework Team
 * @version 1.0
 */
@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    /** 管理员用户名（从配置文件读取） */
    @Value("${app.admin.username}")
    private String adminUsername;

    /** 管理员密码（从配置文件读取） */
    @Value("${app.admin.password}")
    private String adminPassword;

    /** 管理员邮箱（从配置文件读取） */
    @Value("${app.admin.email}")
    private String adminEmail;

    /** 普通用户名（从配置文件读取） */
    @Value("${app.user.username}")
    private String userUsername;

    /** 普通用户密码（从配置文件读取） */
    @Value("${app.user.password}")
    private String userPassword;

    /** 普通用户邮箱（从配置文件读取） */
    @Value("${app.user.email}")
    private String userEmail;

    /**
     * 数据初始化 Bean
     *
     * <p>在应用启动后自动执行，初始化系统基础数据。</p>
     *
     * @param roleRepository 角色数据访问接口
     * @param permissionRepository 权限数据访问接口
     * @param userRepository 用户数据访问接口
     * @param passwordEncoder 密码编码器
     * @param postImportService 文章导入服务
     * @return CommandLineRunner 实例
     */
    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository,
                                      PermissionRepository permissionRepository,
                                      UserRepository userRepository,
                                      PasswordEncoder passwordEncoder,
                                      PostImportService postImportService) {
    return args -> {
            // 1. 创建权限
            Permission postCreate = createPermissionIfNotFound(permissionRepository, AppConstants.PERM_POST_CREATE);
            Permission postRead = createPermissionIfNotFound(permissionRepository, AppConstants.PERM_POST_READ);
            Permission postUpdate = createPermissionIfNotFound(permissionRepository, AppConstants.PERM_POST_UPDATE);
            Permission postDelete = createPermissionIfNotFound(permissionRepository, AppConstants.PERM_POST_DELETE);
            Permission commentCreate = createPermissionIfNotFound(permissionRepository, AppConstants.PERM_COMMENT_CREATE);
            Permission commentDelete = createPermissionIfNotFound(permissionRepository, AppConstants.PERM_COMMENT_DELETE);

            // 2. Create Roles
            Role adminRole = createRoleIfNotFound(roleRepository, AppConstants.ROLE_ADMIN,
                new HashSet<>(Arrays.asList(postCreate, postRead, postUpdate, postDelete, commentCreate, commentDelete)));

            Role userRole = createRoleIfNotFound(roleRepository, AppConstants.ROLE_USER,
                new HashSet<>(Arrays.asList(postCreate, postRead, commentCreate)));

            // 3. Create Admin User if not exists
            if (userRepository.findByUsername(adminUsername).isEmpty()) {
                User admin = new User();
                admin.setUsername(adminUsername);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setEmail(adminEmail);
                admin.setRoles(new HashSet<>(Arrays.asList(adminRole)));
                userRepository.save(admin);
                logger.info("Admin user created.");
            }

            // 4. Create Standard User if not exists
            if (userRepository.findByUsername(userUsername).isEmpty()) {
                User user = new User();
                user.setUsername(userUsername);
                user.setPassword(passwordEncoder.encode(userPassword));
                user.setEmail(userEmail);
                user.setRoles(new HashSet<>(Arrays.asList(userRole)));
                userRepository.save(user);
                logger.info("Standard user created.");
            }

            // 5. Import Posts from Markdown
            postImportService.importPostsFromResources();
        };
    }

    @Transactional
    Permission createPermissionIfNotFound(PermissionRepository permissionRepository, String name) {
        return permissionRepository.findByName(name).orElseGet(() -> {
            Permission permission = new Permission();
            permission.setName(name);
            return permissionRepository.save(permission);
        });
    }

    @Transactional
    Role createRoleIfNotFound(RoleRepository roleRepository, String name, Set<Permission> permissions) {
        return roleRepository.findByName(name).orElseGet(() -> {
            Role role = new Role();
            role.setName(name);
            role.setPermissions(permissions);
            return roleRepository.save(role);
        });
    }
}
