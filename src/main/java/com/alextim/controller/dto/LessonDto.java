package com.alextim.controller.dto;

import com.alextim.domain.Lesson;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data @NoArgsConstructor @AllArgsConstructor
public class LessonDto {

    public static final String JSON_EXAMPLE ="{ \"title\" : \"lesson\", \"homework\" : \"homework\", \"courseId\" : 1}";

    @Setter(AccessLevel.NONE)
    private long id;

    @NotNull(message = "title cannot be null") @NotEmpty @NotBlank
    private String title;

    private String homework;

    @Min(value = 1, message = "courseId must be longer than 0") @NotNull(message = "courseId cannot be null")
    private long courseId;

    public static LessonDto toLessonDto(Lesson lesson) {
        return new LessonDto(lesson.getId(), lesson.getTitle(), lesson.getHomework(), lesson.getCourse().getId());
    }
}