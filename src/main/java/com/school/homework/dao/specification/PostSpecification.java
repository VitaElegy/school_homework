package com.school.homework.dao.specification;

import com.school.homework.dto.PostSearchCriteria;
import com.school.homework.entity.Post;
import com.school.homework.entity.Tag;
import com.school.homework.entity.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class PostSpecification {

    public static Specification<Post> withCriteria(PostSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // We need to fetch associated data to avoid N+1, but Specification API for fetching is tricky with count query.
            // EntityGraph is better for fetching.
            // For filtering:

            if (StringUtils.hasText(criteria.getQuery())) {
                String search = "%" + criteria.getQuery().toLowerCase() + "%";
                Predicate titleLike = cb.like(cb.lower(root.get("title")), search);
                Predicate contentLike = cb.like(cb.lower(root.get("content")), search);
                predicates.add(cb.or(titleLike, contentLike));
            }

            if (StringUtils.hasText(criteria.getTag())) {
                Join<Post, Tag> tagsJoin = root.join("tags", JoinType.INNER);
                predicates.add(cb.equal(tagsJoin.get("name"), criteria.getTag()));
            }

            if (StringUtils.hasText(criteria.getAuthorUsername())) {
                Join<Post, User> authorJoin = root.join("author", JoinType.INNER);
                predicates.add(cb.equal(authorJoin.get("username"), criteria.getAuthorUsername()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

