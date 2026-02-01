package com.service.Blogs;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.dto.CommentRequestDTO;
import com.dto.CommentResponseDTO;
import com.entity.User;
import com.entity.Blogs.Blog;
import com.entity.Blogs.CommentBlog;
import com.repository.UserRepository;
import com.repository.Blogs.BlogRepository;
import com.repository.Blogs.CommentBlogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentBlogService {
    


    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    private final CommentBlogRepository commentRepository;


    public CommentResponseDTO creatComment( UUID user_id,CommentRequestDTO dto) throws Exception {

        Blog blog = blogRepository.findById(dto.getId_blog())
            .orElseThrow(() -> new RuntimeException("Blog not found"));

        
        User user = userRepository.findById(user_id)
            .orElseThrow(() -> new RuntimeException("User not found"));

            
        CommentBlog comment = new CommentBlog();
        comment.setComentblog(dto.getComment());
        comment.setBlog(blog);
        comment.setUser(user);

        commentRepository.save(comment);

        CommentResponseDTO data = new CommentResponseDTO();
        data.setComment(comment.getComentblog());
        data.setCreatorUsername(user.getUsername());
        data.setUrlString(user.getImageUrl());
        
        return data;
    }

}
