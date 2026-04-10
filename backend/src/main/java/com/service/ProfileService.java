package com.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import com.repository.FollowersRepository;
import com.repository.NotificationsRepository;
import com.repository.UserRepository;
import com.repository.Blogs.BlogRepository;
import com.util.TypeNotifications;
import com.util.UserStatus;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.dto.Response.ProfileResponseDTO;
import com.entity.Followers;
import com.entity.Notifications;
import com.entity.User;

@RequiredArgsConstructor
@Service
public class ProfileService {
        private final FollowersRepository followersRepository;
        private final UserRepository userRepository;
        private final BlogRepository blogRepository;
        private final NotificationsRepository notificationsRepository;

        @Transactional(readOnly = true)
        public ProfileResponseDTO getProfileData(String username, UUID user_id) throws Exception {
                if (user_id == null) {
                        throw new RuntimeException("User ID cannot be null");
                }
                User userReq = userRepository.findById(user_id)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                if (user.getStatus().equals(UserStatus.BANNED)) {
                        throw new RuntimeException("This User banned from this platform.");
                }

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
        public void followed(String username, UUID user_id) throws Exception {

                if (user_id == null) {
                        throw new RuntimeException("User ID cannot be null");
                }

                User follower = userRepository.findById(user_id)
                                .orElseThrow(() -> new RuntimeException("User not found"));
                if (follower.getStatus() == UserStatus.BANNED) {
                        throw new RuntimeException("You are banned from this platform.");
                }
                User followed = userRepository.findByUsername(username)
                                .orElseThrow(() -> new RuntimeException("User not found"));
                if (followed.getStatus() == UserStatus.BANNED) {
                        throw new RuntimeException("This User banned from this platform.");
                }
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

                Notifications notif = new Notifications();
                notif.setActive(true);
                notif.setCreatorNf(follower);
                notif.setIntendedUser(followed);
                notif.setType(TypeNotifications.FOLLOW);
                notif.setMessage("started following you");
                notificationsRepository.save(notif);
        }

        @Transactional
        public void unFollow(String username, UUID userId) {

                if (userId == null) {
                        throw new RuntimeException("User ID cannot be null");
                }
                User follower = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                if (follower.getStatus() == UserStatus.BANNED) {
                        throw new RuntimeException("You are banned from this platform.");
                }
                User followed = userRepository.findByUsername(username)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                if (followed.getStatus() == UserStatus.BANNED) {
                        throw new RuntimeException("This User banned from this platform.");
                }
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

}