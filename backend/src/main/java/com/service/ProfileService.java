package com.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.repository.FollowersRepository;
import com.repository.UserRepository;
import com.repository.Blogs.BlogRepository;

import jakarta.transaction.Transactional;

import com.dto.ProfileResponseDTO;
import com.entity.Followers;
import com.entity.User;

@Service
@Transactional
public class ProfileService {

        private final FollowersRepository followersRepository;
        private final UserRepository userRepository;
        private final BlogRepository blogRepository;

        public ProfileService(FollowersRepository followersRepository, UserRepository userRepository,
                        BlogRepository blogRepository) {
                this.followersRepository = followersRepository;
                this.userRepository = userRepository;
                this.blogRepository = blogRepository;
        }

        public ProfileResponseDTO getProfileData(String username, UUID user_id) throws Exception {
                User userReq = userRepository.findById(user_id)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                Long BlogsCont = blogRepository.countByCreatedBy_Id(user.getId());

                boolean isFollower = followersRepository.existsByFollower_IdAndFollowed_Id(user.getId(),
                                userReq.getId());
                boolean isFollowing = followersRepository.existsByFollower_IdAndFollowed_Id(userReq.getId(),
                                user.getId());

                long followersCount = followersRepository.countByFollowed_Id(user.getId());
                long followingCount = followersRepository.countByFollower_Id(user.getId());

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

        public void followed(String username, UUID user_id) throws Exception {

                User follower = userRepository.findById(user_id)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                User followed = userRepository.findByUsername(username)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                if (follower.getId().equals(followed.getId())) {
                        return;
                }

                boolean alreadyFollowing = followersRepository.existsByFollower_IdAndFollowed_Id(
                                follower.getId(),
                                followed.getId());

                if (alreadyFollowing) {
                        throw new RuntimeException("Already following this user");
                }

                Followers followers = new Followers();
                followers.setFollower(follower);
                followers.setFollowed(followed);

                followersRepository.save(followers);

        }

        public void unFollow(String username, UUID userId) {

                User follower = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                User followed = userRepository.findByUsername(username)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                if (follower.getId().equals(followed.getId())) {
                        return;
                }

                boolean exists = followersRepository.existsByFollower_IdAndFollowed_Id(
                                follower.getId(),
                                followed.getId());

                if (!exists) {
                        return;
                }

                followersRepository.deleteByFollower_IdAndFollowed_Id(
                                follower.getId(),
                                followed.getId());
        }

        private List<User> getUsersPaginated(int page, int size) {
                Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
                return userRepository.findAll(pageable).getContent();
        }

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

                return AllUser.stream()
                                .map(user -> {

                                        boolean isFollowing = followingIds.contains(user.getId());
                                        boolean isFollower = followerIds.contains(user.getId());
                                        boolean isYourProfile = userId.equals(user.getId());
                                        Long BlogsCont = blogRepository.countByCreatedBy_Id(user.getId());

                                        return new ProfileResponseDTO(
                                                        user.getUsername(),
                                                        user.getImageUrl(),
                                                        isYourProfile,
                                                        isFollower,
                                                        isFollowing,
                                                        BlogsCont);
                                })
                                .toList();
        }

        public List<ProfileResponseDTO> getFollowersOrFollowing(int page, int size, String username, UUID userId,
                        String mode) {
                if (username == null || username.isEmpty()) {
                        throw new RuntimeException("Invalid user");
                }

                if (mode == null || mode.isEmpty()) {
                        throw new RuntimeException("Error Getting users");
                }

                if (!userRepository.existsById(userId)) {
                        throw new RuntimeException("Invalid user context");
                }

                User profileUser = userRepository.findByUsername(username)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                Pageable pageable = PageRequest.of(page, size);

                List<Followers> follow = new ArrayList();

                if (mode.equals("followers")) {
                        follow = followersRepository.findByFollowed_Id(profileUser.getId(), pageable);
                } else if (mode.equals("following")) {
                        follow = followersRepository.findByFollower_Id(profileUser.getId(), pageable);
                }

                Set<UUID> followingIds = new HashSet<>(followersRepository.findFollowedIdsByFollowerId(userId));

                Set<UUID> followerIds = new HashSet<>(followersRepository.findFollowerIdsByFollowedId(userId));

                return follow.stream()
                                .map(f -> {
                                        User u;
                                        if (mode.equals("followers")) {
                                                u = f.getFollower();
                                        } else {
                                                u = f.getFollowed();
                                        }
                                        boolean isFollowing = followingIds.contains(u.getId());
                                        boolean isFollower = followerIds.contains(u.getId());
                                        boolean isYourProfile = userId.equals(u.getId());
                                        Long BlogsCont = blogRepository.countByCreatedBy_Id(u.getId());

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
}
