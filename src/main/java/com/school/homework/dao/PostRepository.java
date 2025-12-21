package com.school.homework.dao;

import com.school.homework.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    @EntityGraph(attributePaths = {"author", "tags"})
    List<Post> findByAuthorId(Long userId);

    @EntityGraph(attributePaths = {"author", "tags"})
    Page<Post> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String title, String content, Pageable pageable);

    @EntityGraph(attributePaths = {"author", "tags"})
    Page<Post> findByTags_Name(String tagName, Pageable pageable);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void incrementViewCount(Long id);

    @Override
    @EntityGraph(attributePaths = {"author", "tags"})
    Page<Post> findAll(org.springframework.data.jpa.domain.Specification<Post> spec, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"author", "tags"})
    Page<Post> findAll(Pageable pageable);
}
