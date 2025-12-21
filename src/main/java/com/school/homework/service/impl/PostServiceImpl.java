package com.school.homework.service.impl;

import com.school.homework.dao.PostRepository;
import com.school.homework.dao.TagRepository;
import com.school.homework.dao.UserRepository;
import com.school.homework.entity.Post;
import com.school.homework.entity.Tag;
import com.school.homework.entity.User;
import com.school.homework.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository, TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    public Page<Post> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    @Override
    public Page<Post> searchPosts(String query, Pageable pageable) {
        return postRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(query, query, pageable);
    }

    @Override
    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found: " + id));
    }

    @Override
    public Post createPost(Post post, Long userId, String tags) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        post.setAuthor(user);

        if (tags != null && !tags.trim().isEmpty()) {
            String[] tagNames = tags.split(",");
            for (String tagName : tagNames) {
                String cleanName = tagName.trim();
                if (!cleanName.isEmpty()) {
                    Tag tag = tagRepository.findByName(cleanName)
                            .orElseGet(() -> tagRepository.save(new Tag(cleanName)));
                    post.addTag(tag);
                }
            }
        }

        return postRepository.save(post);
    }

    @Override
    public void deletePost(Long id, String username) {
        Post post = getPostById(id);
        // Allow admin or owner to delete
        // Re-fetching user to check roles is one way, or trusting SecurityContext authorities in Controller
        // Here we just check ownership for simplicity as per previous code, but could be enhanced.
        // Actually the controller has @PreAuthorize("hasAuthority('POST_DELETE')"), so the user calling this
        // definitely has permission to invoke the endpoint.
        // However, standard logic usually implies 'POST_DELETE' allows deleting ANY post (admin)
        // OR one's own post.
        // The previous code had strict owner check OR comment about ADMIN.
        // Let's keep it simple: if not owner, and maybe check role?
        // Since we don't have easy access to authorities here without SecurityContextHolder,
        // we'll assume the Controller/Security layer handles "CAN invoke".
        // But business logic "CAN delete THIS specific post" is stricter.

        boolean isOwner = post.getAuthor().getUsername().equals(username);
        // Ideally we check if user is Admin.
        // For now, let's stick to the previous logic but maybe relax it if we can confirm admin?
        // Let's trust the previous implementation's intent but maybe the user wants full features.
        // I'll leave the ownership check for now to be safe, as I don't want to break existing tests if any.

        if (!isOwner) {
             // For Admin override, we'd need to check the current user's roles.
             // We can fetch the user by username.
             User user = userRepository.findByUsername(username).orElseThrow();
             boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_ADMIN"));
             if (!isAdmin) {
                 throw new AccessDeniedException("You are not authorized to delete this post");
             }
        }

        postRepository.deleteById(id);
    }
}
