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

/**
 * 博客控制器
 * 
 * <p>处理博客文章和评论相关的 HTTP 请求。</p>
 * 
 * <p>主要功能：
 * <ul>
 *   <li>文章列表展示和搜索</li>
 *   <li>文章详情查看</li>
 *   <li>文章创建、编辑、删除</li>
 *   <li>评论添加</li>
 * </ul>
 * </p>
 * 
 * <p>路由前缀：/blog</p>
 * 
 * @author School Homework Team
 * @version 1.0
 */
@Controller
@RequestMapping("/blog")
public class BlogController {

    /** 文章服务 */
    private final PostService postService;
    
    /** 评论服务 */
    private final CommentService commentService;
    
    /** Markdown 渲染服务 */
    private final MarkdownService markdownService;

    /**
     * 构造函数注入依赖
     * 
     * @param postService 文章服务
     * @param commentService 评论服务
     * @param markdownService Markdown 渲染服务
     */
    @Autowired
    public BlogController(PostService postService, CommentService commentService, MarkdownService markdownService) {
        this.postService = postService;
        this.commentService = commentService;
        this.markdownService = markdownService;
    }

    // ========== 文章相关路由 ==========

    /**
     * 显示文章列表页面
     * 
     * <p>支持搜索和分页功能。</p>
     * 
     * @param criteria 搜索条件（标题、内容、标签、作者）
     * @param page 页码，从0开始，默认0
     * @param size 每页大小，默认5
     * @param model 视图模型
     * @return 文章列表页面视图
     */
    @GetMapping
    public String listPosts(@ModelAttribute PostSearchCriteria criteria,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "5") int size,
                            Model model) {
        // 创建分页对象，按创建时间倒序排列
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // 执行搜索查询
        Page<Post> postPage = postService.searchPosts(criteria, pageable);

        // 将数据添加到模型中
        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("totalItems", postPage.getTotalElements());
        model.addAttribute("criteria", criteria); // 保留搜索条件，用于表单回填

        return "blog/posts";
    }

    /**
     * 显示文章详情页面
     * 
     * <p>查看文章时自动递增浏览次数，并将 Markdown 内容渲染为 HTML。</p>
     * 
     * @param id 文章ID
     * @param model 视图模型
     * @return 文章详情页面视图
     */
    @GetMapping("/posts/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        // 原子递增浏览次数
        postService.incrementViewCount(id);
        
        // 获取文章
        Post post = postService.getPostById(id);

        // 将 Markdown 内容渲染为 HTML
        String htmlContent = markdownService.renderToHtml(post.getContent());
        model.addAttribute("htmlContent", htmlContent);

        // 添加文章和评论表单到模型
        model.addAttribute("post", post);
        model.addAttribute("newComment", new CommentDto());
        return "blog/post_detail";
    }

    /**
     * 显示创建文章表单
     * 
     * <p>需要 POST_CREATE 权限。</p>
     * 
     * @param model 视图模型
     * @return 创建文章页面视图
     */
    @GetMapping("/posts/new")
    @PreAuthorize("hasAuthority('POST_CREATE')")
    public String createPostForm(Model model) {
        model.addAttribute("post", new PostDto());
        return "blog/create_post";
    }

    /**
     * 处理文章创建请求
     * 
     * <p>需要 POST_CREATE 权限。验证表单数据后创建文章。</p>
     * 
     * @param postDto 文章数据传输对象
     * @param bindingResult 验证结果
     * @param principal 当前登录用户
     * @return 重定向到文章列表页面
     */
    @PostMapping("/posts")
    @PreAuthorize("hasAuthority('POST_CREATE')")
    public String createPost(@Valid @ModelAttribute("post") PostDto postDto, BindingResult bindingResult, Principal principal) {
        // 如果验证失败，返回表单页面显示错误
        if (bindingResult.hasErrors()) {
            return "blog/create_post";
        }
        // 创建文章，当前用户作为作者
        postService.createPost(postDto, principal.getName());
        return "redirect:/blog";
    }

    /**
     * 显示编辑文章表单
     * 
     * <p>需要 POST_UPDATE 权限。只有文章作者可以编辑自己的文章。</p>
     * 
     * @param id 文章ID
     * @param model 视图模型
     * @param principal 当前登录用户
     * @return 编辑文章页面视图或403错误页面
     */
    @GetMapping("/posts/{id}/edit")
    @PreAuthorize("hasAuthority('POST_UPDATE')")
    public String editPostForm(@PathVariable Long id, Model model, Principal principal) {
        PostDto postDto;
        try {
            // 获取文章实体以检查所有权
            Post post = postService.getPostById(id);
            
            // 检查是否为文章作者
            if (!post.getAuthor().getUsername().equals(principal.getName())) {
                return "error/403";
            }
            
            // 转换为DTO用于表单显示
            postDto = postService.getPostDtoById(id);

        } catch (org.springframework.security.access.AccessDeniedException e) {
            return "error/403";
        }

        model.addAttribute("post", postDto);
        return "blog/edit_post";
    }

    /**
     * 处理文章更新请求
     * 
     * <p>需要 POST_UPDATE 权限。只有文章作者可以更新自己的文章。</p>
     * 
     * @param id 文章ID
     * @param postDto 文章数据传输对象
     * @param bindingResult 验证结果
     * @param principal 当前登录用户
     * @param model 视图模型
     * @return 重定向到文章详情页面或返回编辑表单
     */
    @PostMapping("/posts/{id}/edit")
    @PreAuthorize("hasAuthority('POST_UPDATE')")
    public String updatePost(@PathVariable Long id, @Valid @ModelAttribute("post") PostDto postDto, BindingResult bindingResult, Principal principal, Model model) {
        // 如果验证失败，返回表单页面显示错误
        if (bindingResult.hasErrors()) {
            return "blog/edit_post";
        }
        try {
            // 更新文章，服务层会检查所有权
            postService.updatePost(id, postDto, principal.getName());
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return "error/403";
        }
        return "redirect:/blog/posts/" + id;
    }

    /**
     * 处理文章删除请求
     * 
     * <p>需要 POST_DELETE 权限。文章作者可以删除自己的文章，管理员可以删除任何文章。</p>
     * 
     * @param id 文章ID
     * @param principal 当前登录用户
     * @return 重定向到文章列表页面
     */
    @PostMapping("/posts/{id}/delete")
    @PreAuthorize("hasAuthority('POST_DELETE')")
    public String deletePost(@PathVariable Long id, Principal principal) {
        // 删除文章，服务层会检查所有权或管理员权限
        postService.deletePost(id, principal.getName());
        return "redirect:/blog";
    }

    // ========== 评论相关路由 ==========

    /**
     * 处理添加评论请求
     * 
     * <p>需要 COMMENT_CREATE 权限。认证用户可以评论文章。</p>
     * 
     * @param postId 文章ID
     * @param commentDto 评论数据传输对象
     * @param bindingResult 验证结果
     * @param principal 当前登录用户
     * @param model 视图模型
     * @return 重定向到文章详情页面或返回文章详情页面显示错误
     */
    @PostMapping("/posts/{postId}/comments")
    @PreAuthorize("hasAuthority('COMMENT_CREATE')")
    public String addComment(@PathVariable Long postId, @Valid @ModelAttribute("newComment") CommentDto commentDto, BindingResult bindingResult, Principal principal, Model model) {
        // 如果验证失败，重新加载文章以便正确显示错误
        if (bindingResult.hasErrors()) {
            Post post = postService.getPostById(postId);
            model.addAttribute("post", post);
            // newComment 已经通过 @ModelAttribute 添加到模型中，包含错误信息
            return "blog/post_detail";
        }
        // 添加评论，当前用户作为评论作者
        commentService.addComment(commentDto, postId, principal.getName());
        return "redirect:/blog/posts/" + postId;
    }
}
