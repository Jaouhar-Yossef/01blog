package com.service;

import java.util.Optional;
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
import com.util.BlogStatus;
import com.util.ReportType;
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

        if (user_id == null) {
            throw new RuntimeException("User ID cannot be null");
        }

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
        report.setCreatedBy(userReq);
        report.setReason(reportRequest.getReason());
        report.setStatus("PENDING");

        UUID blogid = reportRequest.getReportedBlog();

        if (blogid != null) {
            Blog blogReported = blogRepository.findById(blogid)
                    .orElseThrow(() -> new RuntimeException("Blog not found"));
            if (blogReported.getStatus() == BlogStatus.HIDDEN) {
                throw new RuntimeException("This blog has been hidden by the admin.");
            }
            report.setReportedBlog(blogReported);
            report.setType(ReportType.BLOG);

            Optional<Notifications> existing = notificationsRepository
                    .findFirstByTypeAndCreatorNfAndIntendedUser(
                            TypeNotifications.REPORTEDBLOG, userReq, blogReported.getCreatedBy());

            if (existing.isEmpty()) {
                Notifications notif = new Notifications();
                notif.setActive(true);
                notif.setIntendedBlog(blogReported);
                notif.setType(TypeNotifications.REPORTEDBLOG);
                notif.setCreatorNf(userReq);
                notif.setMessage("Your blog has been reported.");
                notif.setIntendedUser(blogReported.getCreatedBy());
                notificationsRepository.save(notif);
            }
        }

        if (reportRequest.getReportedUser() != null) {
            User userReported = userRepository.findByUsername(reportRequest.getReportedUser())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            if (userReported.getStatus() == UserStatus.BANNED) {
                throw new RuntimeException("This User banned from this platform.");
            }
            report.setReportedUser(userReported);
            report.setType(ReportType.USER);

            Optional<Notifications> existing = notificationsRepository
                    .findFirstByTypeAndCreatorNfAndIntendedUser(
                            TypeNotifications.REPORTEDACCOUNT, userReq, userReported);

            if (existing.isEmpty()) {
                Notifications notif = new Notifications();
                notif.setActive(true);
                notif.setCreatorNf(userReq);
                notif.setType(TypeNotifications.REPORTEDACCOUNT);
                notif.setMessage("Your account has been reported.");
                notif.setIntendedUser(userReported);
                notificationsRepository.save(notif);
            }
        }
        reportRepository.save(report);
    }
}
