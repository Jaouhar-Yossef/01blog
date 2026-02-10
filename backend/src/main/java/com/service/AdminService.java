package com.service;

import org.springframework.stereotype.Service;

import com.dto.AnalyticsDTO;
import com.repository.ReportRepository;
import com.repository.UserRepository;
import com.repository.Blogs.BlogRepository;
import com.repository.Blogs.LikeBlogRepository;
import com.util.Response;

@Service
public class AdminService {

    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final LikeBlogRepository likeBlogRepository;

    public AdminService(BlogRepository blogRepository , UserRepository userRepository,
     ReportRepository reportRepository , LikeBlogRepository likeBlogRepository) {
        this.blogRepository = blogRepository;
        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
        this.likeBlogRepository = likeBlogRepository;
    }
    public Response<?> getAnalytics() {

        Long contsBlogs = blogRepository.count();
        Long contsUsers = userRepository.count();
        Long contsReports = reportRepository.count();
        Long contsAllLikes = likeBlogRepository.count();
        

        AnalyticsDTO data = new AnalyticsDTO(
            contsBlogs,
            contsUsers,
            contsReports,
            contsAllLikes
        );

        return new Response<>(true, "" , data);
    }
}
