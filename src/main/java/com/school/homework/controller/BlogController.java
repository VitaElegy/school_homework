package com.school.homework.controller;

import com.school.homework.dto.CommentDto;
import com.school.homework.dto.PostDto;
import com.school.homework.dto.PostSearchCriteria;
import com.school.homework.entity.Post;
import com.school.homework.entity.Tag;
import com.school.homework.service.CommentService;
import com.school.homework.service.MarkdownService;
import com.school.homework.service.PostService;
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
import java.util.stream.Collectors;

@Controller
@RequestMapping("/blog")
public class BlogController {

    private final PostService postService;
    private final CommentService commentService;
    private final MarkdownService markdownService;

    @Autowired
    public BlogController(PostService postService, CommentService commentService, MarkdownService markdownService) {
        this.postService = postService;
        this.commentService = commentService;
        this.markdownService = markdownService;
    }

    // --- Post Routes ---

    @GetMapping
    public String listPosts(@ModelAttribute PostSearchCriteria criteria,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "5") int size,
                            Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        Page<Post> postPage = postService.searchPosts(criteria, pageable);

        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("totalItems", postPage.getTotalElements());
        model.addAttribute("criteria", criteria); // Add criteria to model to refill form

        return "blog/posts";
    }

    @GetMapping("/posts/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        postService.incrementViewCount(id);
        Post post = postService.getPostById(id);

        // Render Markdown content
        String htmlContent = markdownService.renderToHtml(post.getContent());
        model.addAttribute("htmlContent", htmlContent);

        model.addAttribute("post", post);
        model.addAttribute("newComment", new CommentDto());
        return "blog/post_detail";
    }

    @GetMapping("/posts/new")
    @PreAuthorize("hasAuthority('POST_CREATE')")
    public String createPostForm(Model model) {
        model.addAttribute("post", new PostDto());
        return "blog/create_post";
    }

    @PostMapping("/posts")
    @PreAuthorize("hasAuthority('POST_CREATE')")
    public String createPost(@Valid @ModelAttribute("post") PostDto postDto, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "blog/create_post";
        }
        postService.createPost(postDto, principal.getName());
        return "redirect:/blog";
    }

    @GetMapping("/posts/{id}/edit")
    @PreAuthorize("hasAuthority('POST_UPDATE')")
    public String editPostForm(@PathVariable Long id, Model model, Principal principal) {
        PostDto postDto;
        try {
             // We can retrieve the post via service, service will check existence but we need to check ownership here
             // Or better, let service return DTO and we check ownership or service throws exception?
             // Actually, getPostDtoById just converts. We should check ownership before displaying EDIT form.
             // But getPostById returns Entity.

             // Simplest approach: fetch entity to check owner (fast), then convert to DTO (or use service DTO method)
             Post post = postService.getPostById(id);
             if (!post.getAuthor().getUsername().equals(principal.getName())) {
                  return "error/403";
             }
             postDto = postService.getPostDtoById(id);

        } catch (org.springframework.security.access.AccessDeniedException e) {
            return "error/403";
        }

        model.addAttribute("post", postDto);
        // tagString is now part of postDto
        return "blog/edit_post";
    }

    @PostMapping("/posts/{id}/edit")
    @PreAuthorize("hasAuthority('POST_UPDATE')")
    public String updatePost(@PathVariable Long id, @Valid @ModelAttribute("post") PostDto postDto, BindingResult bindingResult, Principal principal, Model model) {
        if (bindingResult.hasErrors()) {
            return "blog/edit_post";
        }
        try {
            postService.updatePost(id, postDto, principal.getName());
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return "error/403";
        }
        return "redirect:/blog/posts/" + id;
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
    public String addComment(@PathVariable Long postId, @Valid @ModelAttribute("newComment") CommentDto commentDto, BindingResult bindingResult, Principal principal, Model model) {
        if (bindingResult.hasErrors()) {
            // Re-load the post so the page renders correctly with errors
            Post post = postService.getPostById(postId);
            model.addAttribute("post", post);
            // newComment is already in the model with errors because of @ModelAttribute
            return "blog/post_detail";
        }
        commentService.addComment(commentDto, postId, principal.getName());
        return "redirect:/blog/posts/" + postId;
    }
}
