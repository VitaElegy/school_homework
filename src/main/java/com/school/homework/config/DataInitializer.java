package com.school.homework.config;

import com.school.homework.constant.AppConstants;
import com.school.homework.dao.PermissionRepository;
import com.school.homework.dao.RoleRepository;
import com.school.homework.dao.UserRepository;
import com.school.homework.entity.Permission;
import com.school.homework.entity.Role;
import com.school.homework.entity.User;
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

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.user.username}")
    private String userUsername;

    @Value("${app.user.password}")
    private String userPassword;

    @Value("${app.user.email}")
    private String userEmail;

    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository,
                                      PermissionRepository permissionRepository,
                                      UserRepository userRepository,
                                      PasswordEncoder passwordEncoder) {
    return args -> {
            // 1. Create Permissions
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
