package com.service.Blogs;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.dto.Request.CommentRequestDTO;
import com.dto.Response.CommentResponseDTO;
import com.entity.Notifications;
import com.entity.User;
import com.entity.Blogs.Blog;
import com.entity.Blogs.CommentBlog;
import com.repository.NotificationsRepository;
import com.repository.UserRepository;
import com.repository.Blogs.BlogRepository;
import com.repository.Blogs.CommentBlogRepository;
import com.util.BlogStatus;
import com.util.TypeNotifications;
import com.util.UserStatus;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentBlogService {

    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    private final CommentBlogRepository commentRepository;
    private final NotificationsRepository notificationsRepository;

    @Transactional
    public CommentResponseDTO creatComment(UUID user_id, CommentRequestDTO dto) throws Exception {
        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getStatus() == UserStatus.BANNED) {
            throw new RuntimeException("You are banned from this platform.");
        }

        Blog blog = blogRepository.findById(dto.getId_blog())
                .orElseThrow(() -> new RuntimeException("Blog not found"));

        if (blog.getStatus() == BlogStatus.HIDDEN) {
            throw new RuntimeException("This blog has been hidden by the admin.");
        }

        CommentBlog comment = new CommentBlog();
        comment.setComentblog(dto.getComment());
        comment.setBlog(blog);
        comment.setUser(user);

        commentRepository.save(comment);

        CommentResponseDTO data = new CommentResponseDTO();
        data.setComment(comment.getComentblog());
        data.setCreatorUsername(user.getUsername());
        data.setUrlString(user.getImageUrl());

        List<Notifications> existing = notificationsRepository.findNotification(
                TypeNotifications.COMMENT,
                blog,
                blog.getCreatedBy(),
                user);

        if (existing.isEmpty()) {
            Notifications notif = new Notifications();
            notif.setCreatorNf(user);
            notif.setIntendedBlog(blog);
            notif.setIntendedUser(blog.getCreatedBy());
            notif.setType(TypeNotifications.COMMENT);
            notif.setMessage("commented on your blog");
            notificationsRepository.save(notif);
        }

        return data;
    }

    @Transactional(readOnly = true)
    private List<CommentBlog> getCommentPaginated(int page, int size, UUID id_blog) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return commentRepository.findByBlogId(id_blog, pageable);
    }

    public List<CommentResponseDTO> getTheComment(UUID user_id, int page, int size, UUID id_blog) throws Exception {

        userRepository.findById(user_id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        blogRepository.findById(id_blog)
                .orElseThrow(() -> new RuntimeException("Blog not found"));

        List<CommentBlog> Comments = this.getCommentPaginated(page, size, id_blog);
        List<CommentResponseDTO> CommentsDTO = Comments.stream()
                .map(comment -> {
                    return new CommentResponseDTO(
                            comment.getId(),
                            comment.getComentblog(),
                            comment.getUser().getImageUrl(),
                            comment.getUser().getUsername());
                })
                .toList();
        return CommentsDTO;
    }
}
