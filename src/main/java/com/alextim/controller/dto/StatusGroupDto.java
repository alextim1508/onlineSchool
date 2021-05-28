package com.alextim.controller.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class StatusGroupDto {

    public static final String JSON_EXAMPLE ="{\"groupId\" : 1, \"status\" : \"START\"}";

    @Min(value = 1, message = "groupId cannot be less than 0") @NotNull
    private long groupId;

    @NotNull(message = "status cannot be null") @NotEmpty @NotBlank
    private String status;
}
