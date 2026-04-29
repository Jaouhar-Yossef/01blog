package com.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.dto.Request.DeleteReportsRequest;
import com.dto.Request.UpdateBlogsStatusRequestDTO;
import com.dto.Request.UpdateReportsRequest;
import com.dto.Request.UpdateStatusBlogRequest;
import com.dto.Response.AnalyticsDTO;
import com.dto.Response.BlogsToAdminDTO;
import com.dto.Response.ProfileResponseDTO;
import com.dto.Response.ReportsDTO;
import com.dto.Response.UsersToAdminDTO;
import com.repository.FollowersRepository;
import com.repository.NotificationsRepository;
import com.repository.ReportRepository;
import com.entity.Notifications;
import com.entity.Report;
import com.entity.User;
import com.entity.Blogs.Blog;
import com.repository.UserRepository;
import com.repository.Blogs.BlogRepository;
import com.repository.Blogs.LikeBlogRepository;
import com.util.BlogStatus;
import com.util.ReportType;
import com.util.Response;
import com.util.TypeNotifications;
import com.util.UserStatus;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@SuppressWarnings("null")
@RequiredArgsConstructor
@Service
public class AdminService {

    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final LikeBlogRepository likeBlogRepository;
    private final NotificationsRepository notificationsRepository;
    private final FollowersRepository followersRepository;

    @Transactional(readOnly = true)
    public ProfileResponseDTO getProfileData(String username, UUID user_id) throws Exception {
        if (user_id == null) {
            throw new RuntimeException("User ID cannot be null");
        }
        User userReq = userRepository.findById(user_id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));


        Long BlogsCont = blogRepository.countByCreatedBy_Id(user.getId());

        boolean isFollower = followersRepository.existsByFollower_IdAndFollowed_Id(user.getId(),
                userReq.getId());
        boolean isFollowing = followersRepository.existsByFollower_IdAndFollowed_Id(userReq.getId(),
                user.getId());

        long followersCount = followersRepository.countFollowerNotBanned(user.getId());
        long followingCount = followersRepository.countFollowingNotBanned(user.getId());

        boolean isYou = userReq.getId().equals(user.getId());
        ProfileResponseDTO data = new ProfileResponseDTO(
                user.getUsername(),
                user.getImageUrl(),
                isYou,
                isFollower,
                isFollowing,
                followersCount,
                followingCount,
                BlogsCont);
        return data;
    }

    @Transactional
    public Response<?> updateReport(UpdateReportsRequest request) {
        Report report = reportRepository.findById(request.getReport_id())
                .orElseThrow(() -> new RuntimeException("Report not found"));

        if (report.getStatus().equals(request.getStatus())) {
            return new Response<>(true, "you don't change anything ??");
        }
        if (!(request.getStatus().equals("DECLAINED") || request.getStatus().equals("RESOLVED")
                || request.getStatus().equals("PENDING"))) {
            throw new RuntimeException("status Report is PENDING or RESOLVED or DECLAINED");
        }
        report.setStatus(request.getStatus());
        return new Response<>(true, "update successfully!");
    }

    @Transactional
    public Response<?> updateStatusBlog(UpdateBlogsStatusRequestDTO request) {

        Blog blog = blogRepository.findById(request.getBlog_id())
                .orElseThrow(() -> new RuntimeException("Blog not found"));
        if (blog.getStatus().equals(request.getStatus())) {
            throw new RuntimeException("you don't change anything ??");
        }
        if (!request.getStatus().equals(BlogStatus.SHOW) && !request.getStatus().equals(BlogStatus.HIDDEN)) {
            throw new RuntimeException("status blog must be show or hidden!");
        }

        blog.setStatus(request.getStatus());

        Optional<Notifications> existing = notificationsRepository.findActiveNotification(
                TypeNotifications.BLOGHIDDEN,
                blog,
                blog.getCreatedBy());

        if (request.getStatus().equals(BlogStatus.HIDDEN)) {

            if (existing.isEmpty()) {
                Notifications notif = new Notifications();
                notif.setActive(true);
                notif.setIntendedBlog(blog);
                notif.setIntendedUser(blog.getCreatedBy());
                notif.setType(TypeNotifications.BLOGHIDDEN);
                notif.setMessage("has been hidden by admin");
                notificationsRepository.save(notif);
            }
        }

        if (request.getStatus().equals(BlogStatus.SHOW)) {
            existing.ifPresent(n -> notificationsRepository.deleteByTypeAndIntendedBlogAndIntendedUser(
                    TypeNotifications.BLOGHIDDEN,
                    blog,
                    blog.getCreatedBy()));
        }

        return new Response<>(true, "update successfully!");
    }

    @Transactional
    public Response<?> updateStatusUser(UpdateStatusBlogRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getStatus().equals(request.getStatus())) {
            return new Response<>(false, "you don't change anything ??");
        }
        if (request.getStatus().equals(UserStatus.ACTIVE) && request.getStatus().equals(UserStatus.BANNED)) {
            return new Response<>(false, "status User must be ACTIVE or BANNED!");
        }
        if (user.getStatus().equals(UserStatus.ADMIN)) {
            return new Response<>(false, "You are the Admin :)");
        }
        user.setStatus(request.getStatus());
        return new Response<>(true, "update successfully!");
    }

    @Transactional
    public boolean deleteUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
        return true;
    }

    @Transactional
    public boolean deleteReport(DeleteReportsRequest request) {

        Report report = reportRepository.findById(request.getReport_id())
                .orElseThrow(() -> new RuntimeException("Report not found"));
        reportRepository.delete(report);
        return true;
    }

    @Transactional(readOnly = true)
    private List<Report> getReportsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return reportRepository.findAll(pageable).getContent();
    }

    @Transactional(readOnly = true)
    private List<User> getUsersPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return userRepository.findAll(pageable).getContent();
    }

    @Transactional(readOnly = true)
    public Response<?> getUsers(int page, int size) {

        List<User> AllUser = this.getUsersPaginated(page, size)
                .stream()
                .filter(user -> !user.getRole().equals("ADMIN"))
                .toList();

        List<UsersToAdminDTO> data = AllUser.stream()
                .map(user -> {
                    return new UsersToAdminDTO(
                            user.getUsername(),
                            user.getEmail(),
                            user.getRole(),
                            user.getStatus());
                })
                .toList();
        return new Response<>(true, "", data);
    }

    @Transactional(readOnly = true)
    public Response<?> getReports(int page, int size) {
        List<Report> listReports = getReportsPaginated(page, size);
        List<ReportsDTO> data = listReports.stream()
                .map(report -> {

                    boolean st = userRepository.existsById(report.getCreatedBy().getId());
                    if (!st) {
                        return null;
                    }
                    String ReportCreatby = report.getCreatedBy().getUsername();
                    if (report.getType().equals(ReportType.BLOG)) {

                        boolean existsBlog = blogRepository.existsById(report.getReportedBlog().getId());
                        if (!existsBlog) {
                            return null;
                        }
                        UUID reportedBlog = report.getReportedBlog().getId();

                        return new ReportsDTO(
                                report.getId(),
                                report.getType(),
                                ReportCreatby,
                                reportedBlog,
                                "",
                                report.getReason(),
                                report.getStatus());
                    }

                    if (report.getType().equals(ReportType.USER)) {

                        boolean existsUser = userRepository.existsById(report.getReportedUser().getId());
                        if (!existsUser) {
                            return null;
                        }
                        String reporteduser = report.getReportedUser().getUsername();

                        return new ReportsDTO(
                                report.getId(),
                                report.getType(),
                                ReportCreatby,
                                null,
                                reporteduser,
                                report.getReason(),
                                report.getStatus());
                    }

                    return null;
                })
                .toList();

        return new Response<>(true, "", data);
    }

    @Transactional(readOnly = true)
    public Response<?> getAnalytics() {
        Long contsBlogs = blogRepository.count();
        Long contsUsers = userRepository.count();
        Long contsReports = reportRepository.count();
        Long contsAllLikes = likeBlogRepository.count();
        AnalyticsDTO data = new AnalyticsDTO(
                contsBlogs,
                contsUsers,
                contsReports,
                contsAllLikes);
        return new Response<>(true, "", data);
    }

    private List<Blog> getBlogsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        return blogRepository.findAll(pageable).getContent();
    }

    @Transactional(readOnly = true)
    public Response<?> getBlogs(int page, int size) {
        List<Blog> listBlogs = getBlogsPaginated(page, size);
        List<BlogsToAdminDTO> data = listBlogs.stream()
                .map(blog -> {

                    User user = blog.getCreatedBy();
                    return new BlogsToAdminDTO(
                            blog.getId(),
                            blog.getTitle(),
                            user.getUsername(),
                            blog.getStatus(),
                            blog.getUpdatedAt());
                })
                .toList();
        return new Response<>(true, "", data);
    }
}
