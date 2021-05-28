package com.alextim.controller.dto;

import com.alextim.domain.Group;
import lombok.*;

import javax.validation.constraints.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class GroupDto {

    public static final String JSON_EXAMPLE = "{\"title\" : \"group\", \"courseId\" : 1}";

    @Setter(AccessLevel.NONE)
    private long id;

    @NotNull(message = "title cannot be null") @NotEmpty @NotBlank
    private String title;

    @NotNull(message = "courseId cannot be null") @Min(value = 1, message = "courseId must be longer than 0")
    private long courseId;

    private String status;

    public static GroupDto toGroupDto(Group group) {
        return new GroupDto(group.getId(), group.getTitle(), group.getCourse().getId(), group.getStatus().toString());
    }
}
