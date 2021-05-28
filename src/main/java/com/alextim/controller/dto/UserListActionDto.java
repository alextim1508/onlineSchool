package com.alextim.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data @NoArgsConstructor @AllArgsConstructor
public class UserListActionDto {

    public static final String JSON_EXAMPLE = "{\"usersId\" : [1, 2], \"action\" : \"ADD\"}";

    public enum Action {
        ADD, SUB
    }

    @NotNull(message = "usersId cannot be null") @NotEmpty
    private int[] usersId;

    @NotNull(message = "isAdd cannot be null") @NotEmpty
    private String action;
}
