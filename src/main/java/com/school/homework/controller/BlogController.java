package com.school.homework.controller;

import com.school.homework.entity.Comment;
import com.school.homework.entity.Post;
import com.school.homework.entity.User;
import com.school.homework.service.BlogService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.security.Principal;

@Controller
@RequestMapping("/blog")
public class BlogController {

    private final BlogService blogService;

    @Autowired
    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    // --- Post Routes ---

    @GetMapping
    public String listPosts(@RequestParam(required = false) String query, Model model) {
        if (query != null && !query.trim().isEmpty()) {
            model.addAttribute("posts", blogService.searchPosts(query));
            model.addAttribute("query", query);
        } else {
            model.addAttribute("posts", blogService.getAllPosts());
        }
        return "blog/posts";
    }

    @GetMapping("/posts/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        Post post = blogService.getPostById(id);
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
        User user = blogService.findUserByUsername(principal.getName());
        blogService.createPost(post, user.getId());
        return "redirect:/blog";
    }

    // --- Comment Routes ---

    @PostMapping("/posts/{postId}/comments")
    @PreAuthorize("hasAuthority('COMMENT_CREATE')")
    public String addComment(@PathVariable Long postId, @ModelAttribute Comment comment, Principal principal) {
        User user = blogService.findUserByUsername(principal.getName());
        blogService.addComment(comment, postId, user.getId());
        return "redirect:/blog/posts/" + postId;
    }
}
