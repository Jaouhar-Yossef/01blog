package com.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.dto.Request.ReportRequest;
import com.entity.Report;
import com.entity.User;
import com.entity.Blogs.Blog;
import com.repository.ReportRepository;
import com.repository.UserRepository;
import com.repository.Blogs.BlogRepository;

@Service
public class ReportService {

    private UserRepository userRepository;
    private BlogRepository blogRepository;
    private ReportRepository reportRepository;

    public ReportService(UserRepository userRepository, BlogRepository blogRepository,
            ReportRepository reportRepository) {
        this.userRepository = userRepository;
        this.blogRepository = blogRepository;
        this.reportRepository = reportRepository;
    }

    public void creatReport(UUID user_id, ReportRequest reportRequest) throws Exception {
        User userReq = userRepository.findById(user_id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if ("BANNED".equals(userReq.getStatus())) {
            throw new RuntimeException("You are banned from this platform.");
        }
        Report report = new Report();

        report.setCreatedBy(userReq);
        report.setReason(reportRequest.getReason());
        report.setStatus("PENDING");

        if (reportRequest.getReportedBlog() != null) {
            Blog blogReported = blogRepository.findById(reportRequest.getReportedBlog())
                    .orElseThrow(() -> new RuntimeException("Blog not found"));
            report.setReportedBlog(blogReported);
            report.setType("BLOG");
        }

        if (reportRequest.getReportedUser() != null) {
            User userReported = userRepository.findByUsername(reportRequest.getReportedUser())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            report.setReportedUser(userReported);
            report.setType("USER");
        }

        reportRepository.save(report);

    }
}
