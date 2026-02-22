package com.dto.Request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PageableDTO {
    @NotNull(message = "page cannot be null")
    private Integer page;
    @NotNull(message = "size cannot be null")
    private Integer size;
}
