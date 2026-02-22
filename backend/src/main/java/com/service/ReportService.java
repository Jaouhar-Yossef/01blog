package com.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.dto.Request.ReportRequest;
import com.entity.Notifications;
import com.entity.Report;
import com.entity.User;
import com.entity.Blogs.Blog;
import com.repository.NotificationsRepository;
import com.repository.ReportRepository;
import com.repository.UserRepository;
import com.repository.Blogs.BlogRepository;
import com.util.TypeNotifications;

import jakarta.transaction.Transactional;

@Service
public class ReportService {

    private UserRepository userRepository;
    private BlogRepository blogRepository;
    private ReportRepository reportRepository;
    private NotificationsRepository notificationsRepository;

    public ReportService(UserRepository userRepository, BlogRepository blogRepository,
            ReportRepository reportRepository, NotificationsRepository notificationsRepository) {
        this.userRepository = userRepository;
        this.blogRepository = blogRepository;
        this.reportRepository = reportRepository;
        this.notificationsRepository = notificationsRepository;
    }

    @Transactional
    public void creatReport(UUID user_id, ReportRequest reportRequest) throws Exception {
        User userReq = userRepository.findById(user_id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if ("BANNED".equals(userReq.getStatus())) {
            throw new RuntimeException("You are banned from this platform.");
        }

        if (reportRequest.getReportedBlog() == null && reportRequest.getReportedUser() == null) {
            throw new RuntimeException("You must report a blog or a user");
        }

        if (reportRequest.getReportedBlog() != null
                && reportRequest.getReportedUser() != null) {
            throw new RuntimeException("You cannot report blog and user together");
        }

        Report report = new Report();
        Notifications notif = new Notifications();

        report.setCreatedBy(userReq);
        report.setReason(reportRequest.getReason());
        report.setStatus("PENDING");

        if (reportRequest.getReportedBlog() != null) {
            Blog blogReported = blogRepository.findById(reportRequest.getReportedBlog())
                    .orElseThrow(() -> new RuntimeException("Blog not found"));
            report.setReportedBlog(blogReported);
            report.setType("BLOG");

            notif.setIntended_Blog(blogReported);
            notif.setType(TypeNotifications.REPORTEDBLOG);
            notif.setMessage("Your blog has been reported.");
            notif.setIntended_User(blogReported.getCreatedBy());
        }

        if (reportRequest.getReportedUser() != null) {
            User userReported = userRepository.findByUsername(reportRequest.getReportedUser())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            report.setReportedUser(userReported);
            report.setType("USER");

            notif.setType(TypeNotifications.REPORTEDACCOUNT);
            notif.setMessage("Your account has been reported.");
            notif.setIntended_User(userReported);
        }
        reportRepository.save(report);
        notificationsRepository.save(notif);
    }
}
