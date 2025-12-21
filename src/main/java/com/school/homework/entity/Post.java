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

@Entity
@Table(name = "posts")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"tags", "comments", "author"})
@NoArgsConstructor
@AllArgsConstructor
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Title cannot be empty")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Content cannot be empty")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Status cannot be null")
    private PostStatus status = PostStatus.DRAFT;

    @Column(columnDefinition = "integer default 0")
    private int viewCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User author;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "post_tags",
        joinColumns = @JoinColumn(name = "post_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    public void addTag(Tag tag) {
        this.tags.add(tag);
        tag.getPosts().add(this);
    }

    public void removeTag(Tag tag) {
        this.tags.remove(tag);
        tag.getPosts().remove(this);
    }
}
