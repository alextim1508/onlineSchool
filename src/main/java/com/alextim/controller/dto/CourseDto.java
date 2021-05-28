package com.alextim.controller.dto;

import com.alextim.domain.Course;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data @NoArgsConstructor @AllArgsConstructor
public class CourseDto {

    public static final String JSON_EXAMPLE = "{ \"title\" : \"course\", \"description\" : \"description\" } ";

    @Setter(AccessLevel.NONE)
    private long id;

    @NotNull(message = "title cannot be null") @NotEmpty @NotBlank
    private String title;

    @NotNull(message = "description cannot be null") @NotEmpty @NotBlank
    private String description;

    public static CourseDto toCourseDto(Course course) {
        return new CourseDto(course.getId(), course.getTitle(), course.getDescription());
    }
}
