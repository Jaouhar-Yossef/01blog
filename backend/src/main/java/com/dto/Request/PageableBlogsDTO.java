package com.dto.Request;

import com.util.BlogMode;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PageableBlogsDTO {
    @NotNull(message = "page cannot be null")
    private Integer page;
    @NotNull(message = "size cannot be null")
    private Integer size;
    @NotNull(message = "Mode cannot be null")
    private BlogMode mode;
}
