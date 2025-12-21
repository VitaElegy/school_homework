package com.school.homework.service.impl;

import com.school.homework.dto.PostDto;
import com.school.homework.entity.Post;
import com.school.homework.service.PostImportService;
import com.school.homework.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PostImportServiceImpl implements PostImportService {

    private static final Logger logger = LoggerFactory.getLogger(PostImportServiceImpl.class);
    private final PostService postService;

    public PostImportServiceImpl(PostService postService) {
        this.postService = postService;
    }

    @Override
    @Transactional
    public void importPostsFromResources() throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:posts/*.md");

        logger.info("Found {} markdown files to import.", resources.length);

        for (Resource resource : resources) {
            try {
                importSinglePost(resource);
            } catch (Exception e) {
                logger.error("Failed to import post from file: {}", resource.getFilename(), e);
            }
        }
    }

    private void importSinglePost(Resource resource) throws IOException {
        String content = readResource(resource);
        ParsedPost parsed = parseFrontMatter(content);

        if (parsed.title == null || parsed.author == null) {
            logger.warn("Skipping {} due to missing Front Matter (title or author)", resource.getFilename());
            return;
        }

        // Check deduplication (Simple logic: Check if a post with same title exists)
        try {
            // Note: Ideally PostService should expose a method to check existence or find by title
            // But for now, we rely on search or we just try to create. 
            // Better: Let's assume we want to skip if title exists to avoid duplicates on every restart.
            boolean exists = postService.getAllPosts(org.springframework.data.domain.Pageable.unpaged())
                    .stream()
                    .anyMatch(p -> p.getTitle().equals(parsed.title));

            if (exists) {
                logger.info("Post '{}' already exists. Skipping.", parsed.title);
                return;
            }

            PostDto dto = new PostDto();
            dto.setTitle(parsed.title);
            dto.setContent(parsed.body);
            dto.setTagString(parsed.tags);
            dto.setStatus(com.school.homework.enums.PostStatus.PUBLISHED);

            postService.createPost(dto, parsed.author);
            logger.info("Successfully imported post: {}", parsed.title);

        } catch (Exception e) {
            logger.error("Error creating post '{}'", parsed.title, e);
        }
    }

    private String readResource(Resource resource) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        }
    }

    private static class ParsedPost {
        String title;
        String author;
        String tags;
        String body;
    }

    private ParsedPost parseFrontMatter(String fullContent) {
        ParsedPost result = new ParsedPost();
        
        // Regex to match YAML Front Matter between --- and ---
        Pattern pattern = Pattern.compile("^---\\n(.*?)\\n---\\n(.*)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(fullContent);

        if (matcher.find()) {
            String frontMatter = matcher.group(1);
            result.body = matcher.group(2).trim();

            Map<String, String> meta = new HashMap<>();
            for (String line : frontMatter.split("\\n")) {
                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    meta.put(parts[0].trim(), parts[1].trim());
                }
            }

            result.title = meta.get("title");
            result.author = meta.get("author");
            result.tags = meta.get("tags");
        } else {
            // No front matter found, treat whole file as body (or invalid)
            result.body = fullContent;
        }
        return result;
    }
}

