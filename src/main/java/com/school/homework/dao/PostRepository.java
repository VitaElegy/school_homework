package com.school.homework.dao;

import com.school.homework.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    @EntityGraph(attributePaths = {"author", "tags"})
    List<Post> findByAuthorId(Long userId);
    
    @EntityGraph(attributePaths = {"author", "tags"})
    Page<Post> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String title, String content, Pageable pageable);
    
    @EntityGraph(attributePaths = {"author", "tags"})
    Page<Post> findByTags_Name(String tagName, Pageable pageable);
    
    @Override
    @EntityGraph(attributePaths = {"author", "tags"})
    Page<Post> findAll(Pageable pageable);
}
