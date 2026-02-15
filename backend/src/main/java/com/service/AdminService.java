package com.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;

import com.dto.AnalyticsDTO;
import com.dto.BlogsToAdminDTO;
import com.dto.ReportsDTO;
import com.dto.UpdateReportsRequest;
import com.dto.UsersToAdminDTO;
import com.repository.ReportRepository;
import com.entity.Report;
import com.entity.User;
import com.entity.Blogs.Blog;
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

    public AdminService(BlogRepository blogRepository, UserRepository userRepository,
            ReportRepository reportRepository, LikeBlogRepository likeBlogRepository) {
        this.blogRepository = blogRepository;
        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
        this.likeBlogRepository = likeBlogRepository;
    }

    public boolean updateReport(UpdateReportsRequest request) {

        Report r = reportRepository.findById(request.getReport_id())
                .orElseThrow(() -> new RuntimeException("Report not found"));
        r.setStatus(request.getStatus());
        // reportRepository.save(r);
        return true;
    }

    public boolean deleteReport(UpdateReportsRequest request) {
        Report r = reportRepository.findById(request.getReport_id())
                .orElseThrow(() -> new RuntimeException("Report not found"));
        reportRepository.delete(r);
        return true;
    }

    private List<Report> getReportsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return reportRepository.findAll(pageable).getContent();
    }

    private List<User> getUsersPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return userRepository.findAll(pageable).getContent();
    }

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

    public Response<?> getReports(int page, int size) {
        List<Report> listReports = getReportsPaginated(page, size);

        List<ReportsDTO> data = listReports.stream()
                .map(report -> {
                    boolean st = userRepository.existsById(report.getCreatedBy().getId());
                    if (!st) {
                        return null;
                    }
                    String ReportCreatby = report.getCreatedBy().getUsername();

                    if (report.getType().equals("BLOG")) {
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

                    if (report.getType().equals("USER")) {
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
