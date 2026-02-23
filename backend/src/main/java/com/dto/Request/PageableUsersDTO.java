package com.dto.Request;


import com.util.UsersMode;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PageableUsersDTO {
    @NotNull(message = "page cannot be null")
    private Integer page;
    @NotNull(message = "size cannot be null")
    private Integer size;
    @NotNull(message = "Mode cannot be null")
    private UsersMode mode;
    @NotNull(message = "username cannot be null")
    private String username;
}
