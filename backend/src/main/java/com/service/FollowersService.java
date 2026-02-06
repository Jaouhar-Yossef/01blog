package com.service;

import java.util.UUID;

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
public class FollowersService {

    private final FollowersRepository followersRepository;
    private final UserRepository userRepository;
    private final BlogRepository blogRepository;

    public FollowersService(FollowersRepository followersRepository, UserRepository userRepository,
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

        // List<Followers> followers =
        // followersRepository.findByFollowed_Id(userId);

        // List<Followers> following =
        // followersRepository.findByFollower_Id(userId);

        boolean isFollower = followersRepository.existsByFollower_IdAndFollowed_Id(user.getId(), userReq.getId());
        boolean isFollowing = followersRepository.existsByFollower_IdAndFollowed_Id(userReq.getId(), user.getId());

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
            throw new RuntimeException("You cannot follow yourself");
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
            throw new RuntimeException("You cannot unfollow yourself");
        }

        boolean exists = followersRepository.existsByFollower_IdAndFollowed_Id(
                follower.getId(),
                followed.getId());

        if (!exists) {
            throw new RuntimeException("You are not following this user");
        }

        followersRepository.deleteByFollower_IdAndFollowed_Id(
                follower.getId(),
                followed.getId());
    }

}
