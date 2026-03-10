package com.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dto.Request.PageableUsersDTO;
import com.dto.Response.ProfileResponseDTO;
import com.entity.UserDetailsImpl;
import com.util.Response;
import com.util.UsersMode;

import jakarta.validation.Valid;

import com.service.UsersService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class GettingUsersController {

    private final UsersService usersService;

    @GetMapping("gettingUsers")
    private ResponseEntity<Response<?>> getUsers(
            @ModelAttribute @Valid PageableUsersDTO pageableUsersDTO, BindingResult bindingResult,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(new Response<>(false, errorMsg));
        }

        try {
            UUID user_id = userDetails.getUser().getId();

            if (pageableUsersDTO.getMode() == UsersMode.ALLUSERS) {
                
                List<ProfileResponseDTO> data = usersService.getAllUsers(pageableUsersDTO.getPage(), pageableUsersDTO.getSize(), user_id);
                return ResponseEntity.ok().body(new Response<>(true, "sucesfuly", data));

            } else if ( pageableUsersDTO.getMode() == UsersMode.FOLLOWERS || pageableUsersDTO.getMode() == UsersMode.FOLLOWING) {

                List<ProfileResponseDTO> data = usersService.getFollowersOrFollowing(pageableUsersDTO.getPage(), pageableUsersDTO.getSize(),
                 pageableUsersDTO.getUsernameOrSearchWord(),  pageableUsersDTO.getMode() , user_id);
                return ResponseEntity.ok().body(new Response<>(true, "sucesfuly", data));
                
            } else if ( pageableUsersDTO.getMode() == UsersMode.SEARCH) {

                List<ProfileResponseDTO> data = usersService.getUsersBySearch(pageableUsersDTO.getPage(), pageableUsersDTO.getSize(),
                 pageableUsersDTO.getUsernameOrSearchWord(),  pageableUsersDTO.getMode() , user_id);
                return ResponseEntity.ok().body(new Response<>(true, "sucesfuly", data));
                
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }

        return ResponseEntity.badRequest().body(new Response<>(false, "Error Get Users"));
    }

}
