package com.alextim.controller.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class LockUserDto {

    public static final String JSON_EXAMPLE = "{\"userId\" : 1, \"lock\" : \"true\"}";

    @NotNull(message = "userId cannot be null") @Min(value = 1, message = "userId must be longer than 0")
    private long userId;

    @NotNull(message = "title cannot be null")
    private boolean lock;
}
