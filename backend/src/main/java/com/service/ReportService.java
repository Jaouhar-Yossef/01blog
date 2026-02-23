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
import com.util.UserStatus;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ReportService {

    private final UserRepository userRepository;
    private final BlogRepository blogRepository;
    private final ReportRepository reportRepository;
    private final NotificationsRepository notificationsRepository;

    @Transactional
    public void creatReport(UUID user_id, ReportRequest reportRequest) throws Exception {
        User userReq = userRepository.findById(user_id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (userReq.getStatus() == UserStatus.BANNED) {
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
