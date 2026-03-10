package com.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.core.annotation.MergedAnnotations.Search;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dto.Response.ProfileResponseDTO;
import com.entity.Followers;
import com.entity.User;
import com.repository.FollowersRepository;
import com.repository.UserRepository;
import com.repository.Blogs.BlogRepository;
import com.repository.Blogs.UserBlogCount;
import com.util.UserStatus;
import com.util.UsersMode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UsersService {

    private final FollowersRepository followersRepository;
    private final UserRepository userRepository;
    private final BlogRepository blogRepository;

    private List<User> getUsersPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return userRepository.findUsers(pageable);
    }

    @Transactional(readOnly = true)
    public List<ProfileResponseDTO> getAllUsers(int page, int size, UUID userId) {

        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Invalid user context");
        }

        List<User> AllUser = this.getUsersPaginated(page, size)
                .stream()
                .filter(user -> !user.getId().equals(userId))
                .toList();

        Set<UUID> followingIds = new HashSet<>(
                followersRepository.findFollowedIdsByFollowerId(userId));

        Set<UUID> followerIds = new HashSet<>(
                followersRepository.findFollowerIdsByFollowedId(userId));

        List<UUID> userIds = AllUser.stream().map(User::getId).toList();
        List<UserBlogCount> blogCounts = blogRepository.countBlogsForUsers(userIds);

        Map<UUID, Long> blogCountMap = blogCounts.stream()
                .collect(Collectors.toMap(UserBlogCount::getUserId, UserBlogCount::getBlogCount));

        return AllUser.stream()
                .map(user -> {

                    boolean isFollowing = followingIds.contains(user.getId());
                    boolean isFollower = followerIds.contains(user.getId());
                    boolean isYourProfile = userId.equals(user.getId());
                    Long blogsCont = blogCountMap.getOrDefault(user.getId(), 0L);

                    return new ProfileResponseDTO(
                            user.getUsername(),
                            user.getImageUrl(),
                            isYourProfile,
                            isFollower,
                            isFollowing,
                            blogsCont);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProfileResponseDTO> getFollowersOrFollowing(int page, int size, String username,
            UsersMode mode, UUID userId) {
        if (username == null || username.isEmpty()) {
            throw new RuntimeException("Invalid user");
        }

        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Invalid user context");
        }

        User profileUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size);

        List<Followers> follow = new ArrayList<>();

        if (mode == UsersMode.FOLLOWERS) {
            follow = followersRepository.findByFollowed_Id(profileUser.getId(), pageable);
        } else if (mode == UsersMode.FOLLOWING) {
            follow = followersRepository.findByFollower_Id(profileUser.getId(), pageable);
        }

        Set<UUID> followingIds = new HashSet<>(followersRepository.findFollowedIdsByFollowerId(userId));

        Set<UUID> followerIds = new HashSet<>(followersRepository.findFollowerIdsByFollowedId(userId));

        List<UUID> userIds = follow.stream()
                .map(f -> mode == UsersMode.FOLLOWERS ? f.getFollower().getId() : f.getFollowed().getId())
                .toList();
        List<UserBlogCount> blogCounts = blogRepository.countBlogsForUsers(userIds);
        Map<UUID, Long> blogCountMap = blogCounts.stream()
                .collect(Collectors.toMap(UserBlogCount::getUserId, UserBlogCount::getBlogCount));

        return follow.stream()
                .filter(f -> mode == UsersMode.FOLLOWERS ? !f.getFollower().getStatus().equals(UserStatus.BANNED)
                        : !f.getFollowed().getStatus().equals(UserStatus.BANNED))
                .map(f -> {
                    User u;
                    if (mode == UsersMode.FOLLOWERS) {
                        u = f.getFollower();
                    } else {
                        u = f.getFollowed();
                    }
                    boolean isFollowing = followingIds.contains(u.getId());
                    boolean isFollower = followerIds.contains(u.getId());
                    boolean isYourProfile = userId.equals(u.getId());
                    Long BlogsCont = blogCountMap.getOrDefault(u.getId(), 0L);

                    return new ProfileResponseDTO(
                            u.getUsername(),
                            u.getImageUrl(),
                            isYourProfile,
                            isFollower,
                            isFollowing,
                            BlogsCont);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProfileResponseDTO> getUsersBySearch(int page, int size, String searchWord,
            UsersMode mode, UUID userId) {

        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Invalid user context");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        List<User> AllUser = userRepository.findByUsernameContainingIgnoreCase(searchWord, pageable);

        Set<UUID> followingIds = new HashSet<>(
                followersRepository.findFollowedIdsByFollowerId(userId));

        Set<UUID> followerIds = new HashSet<>(
                followersRepository.findFollowerIdsByFollowedId(userId));

        List<UUID> userIds = AllUser.stream().map(User::getId).toList();
        List<UserBlogCount> blogCounts = blogRepository.countBlogsForUsers(userIds);

        Map<UUID, Long> blogCountMap = blogCounts.stream()
                .collect(Collectors.toMap(UserBlogCount::getUserId, UserBlogCount::getBlogCount));

        return AllUser.stream()
                .filter(u -> !u.getStatus().equals(UserStatus.BANNED))
                .map(user -> {
                    boolean isFollowing = followingIds.contains(user.getId());
                    boolean isFollower = followerIds.contains(user.getId());
                    boolean isYourProfile = userId.equals(user.getId());
                    Long blogsCont = blogCountMap.getOrDefault(user.getId(), 0L);

                    return new ProfileResponseDTO(
                            user.getUsername(),
                            user.getImageUrl(),
                            isYourProfile,
                            isFollower,
                            isFollowing,
                            blogsCont);
                })
                .toList();

    }
}
