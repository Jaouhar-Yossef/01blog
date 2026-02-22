package com.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dto.Request.ReportRequest;
import com.entity.UserDetailsImpl;
import com.service.ReportService;
import com.util.Response;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/Report")
public class ReportController {

    private ReportService reportService;

    @PostMapping("/creat")
    private ResponseEntity<Response<?>> creat(
            @Valid @RequestBody ReportRequest reportRequest,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(new Response<>(false, errorMsg));
        }

        if (reportRequest.getReportedBlog() != null && reportRequest.getReportedUser() != null) {
            return ResponseEntity.badRequest().body(new Response<>(false, "Cannot report both blog and user at the same time"));
        }

        try {
            UUID user_id = userDetails.getUser().getId();
            reportService.creatReport(user_id, reportRequest);
            return ResponseEntity.accepted().body(new Response<>(true, "Report Created"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response<>(false, e.getMessage()));
        }
    }

}
