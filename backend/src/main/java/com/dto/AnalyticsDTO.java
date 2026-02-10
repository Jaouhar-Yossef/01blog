package com.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class  AnalyticsDTO {
    private Long contBlogs;
    private Long contUsers;
    private Long contReports;
    private Long contAllLikes;
}