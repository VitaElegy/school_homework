package com.school.homework.config;

import com.school.homework.dao.PermissionRepository;
import com.school.homework.dao.RoleRepository;
import com.school.homework.dao.UserRepository;
import com.school.homework.entity.Permission;
import com.school.homework.entity.Role;
import com.school.homework.entity.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository,
                                      PermissionRepository permissionRepository,
                                      UserRepository userRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. Create Permissions
            Permission postCreate = createPermissionIfNotFound(permissionRepository, "POST_CREATE");
            Permission postRead = createPermissionIfNotFound(permissionRepository, "POST_READ");
            Permission postUpdate = createPermissionIfNotFound(permissionRepository, "POST_UPDATE");
            Permission postDelete = createPermissionIfNotFound(permissionRepository, "POST_DELETE");
            Permission commentCreate = createPermissionIfNotFound(permissionRepository, "COMMENT_CREATE");
            Permission commentDelete = createPermissionIfNotFound(permissionRepository, "COMMENT_DELETE");

            // 2. Create Roles
            Role adminRole = createRoleIfNotFound(roleRepository, "ROLE_ADMIN",
                new HashSet<>(Arrays.asList(postCreate, postRead, postUpdate, postDelete, commentCreate, commentDelete)));

            Role userRole = createRoleIfNotFound(roleRepository, "ROLE_USER",
                new HashSet<>(Arrays.asList(postCreate, postRead, commentCreate)));

            // 3. Create Admin User if not exists
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123")); // Default password, change immediately!
                admin.setEmail("admin@school.com");
                admin.setRoles(new HashSet<>(Arrays.asList(adminRole)));
                userRepository.save(admin);
                System.out.println("Admin user created: admin / admin123");
            }

            // 4. Create Standard User if not exists
            if (userRepository.findByUsername("user").isEmpty()) {
                User user = new User();
                user.setUsername("user");
                user.setPassword(passwordEncoder.encode("password"));
                user.setEmail("user@school.com");
                user.setRoles(new HashSet<>(Arrays.asList(userRole)));
                userRepository.save(user);
                System.out.println("Standard user created: user / password");
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

