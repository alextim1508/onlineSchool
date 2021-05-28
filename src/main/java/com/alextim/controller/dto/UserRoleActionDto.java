package com.alextim.controller.dto;

import com.alextim.security.GrantedAuthorityImpl;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data @NoArgsConstructor @AllArgsConstructor
public class UserRoleActionDto {

    public static String JSON_EXAMPLE = "{ \"id\" : 1, \"action\" : \"ADD\",\"roles\" : [\"ADMIN\", \"TEACHER\"]}";

    public enum Action {
        ADD, SUB
    }

    @Setter(AccessLevel.NONE)
    private long id;

    @NotNull(message = "action cannot be null")
    private Action action;

    @NotNull(message = "roles cannot be null") @NotEmpty
    private GrantedAuthorityImpl.Role[] roles;
}
