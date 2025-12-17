package com.school.homework.controller;

import com.school.homework.entity.Comment;
import com.school.homework.entity.Post;
import com.school.homework.entity.User;
import com.school.homework.service.CommentService;
import com.school.homework.service.PostService;
import com.school.homework.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/blog")
public class BlogController {

    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;

    @Autowired
    public BlogController(PostService postService, UserService userService, CommentService commentService) {
        this.postService = postService;
        this.userService = userService;
        this.commentService = commentService;
    }

    // --- Post Routes ---

    @GetMapping
    public String listPosts(@RequestParam(required = false) String query,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "5") int size,
                            Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> postPage;

        if (query != null && !query.trim().isEmpty()) {
            postPage = postService.searchPosts(query, pageable);
            model.addAttribute("query", query);
        } else {
            postPage = postService.getAllPosts(pageable);
        }

        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("totalItems", postPage.getTotalElements());

        return "blog/posts";
    }

    @GetMapping("/posts/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        Post post = postService.getPostById(id);
        model.addAttribute("post", post);
        model.addAttribute("newComment", new Comment());
        return "blog/post_detail";
    }

    @GetMapping("/posts/new")
    @PreAuthorize("hasAuthority('POST_CREATE')")
    public String createPostForm(Model model) {
        model.addAttribute("post", new Post());
        return "blog/create_post";
    }

    @PostMapping("/posts")
    @PreAuthorize("hasAuthority('POST_CREATE')")
    public String createPost(@Valid @ModelAttribute Post post, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "blog/create_post";
        }
        User user = userService.findUserByUsername(principal.getName());
        postService.createPost(post, user.getId());
        return "redirect:/blog";
    }

    @PostMapping("/posts/{id}/delete")
    @PreAuthorize("hasAuthority('POST_DELETE')")
    public String deletePost(@PathVariable Long id, Principal principal) {
        postService.deletePost(id, principal.getName());
        return "redirect:/blog";
    }

    // --- Comment Routes ---

    @PostMapping("/posts/{postId}/comments")
    @PreAuthorize("hasAuthority('COMMENT_CREATE')")
    public String addComment(@PathVariable Long postId, @ModelAttribute Comment comment, Principal principal) {
        User user = userService.findUserByUsername(principal.getName());
        commentService.addComment(comment, postId, user.getId());
        return "redirect:/blog/posts/" + postId;
    }
}
