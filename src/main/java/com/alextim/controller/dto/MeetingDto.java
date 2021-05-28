package com.alextim.controller.dto;

import com.alextim.domain.Meeting;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data @NoArgsConstructor @AllArgsConstructor
public class MeetingDto {

    public static final String JSON_EXAMPLE ="{\"date\" : \"2019-15-08 20:00:00\",  \"lessonId\" : 1, \"groupId\" : 1}";

    @Setter(AccessLevel.NONE)
    private long id;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "date cannot be null") @Future
    private Date date;

    @NotNull(message = "lessonId cannot be null") @Min(value = 1, message = "lessonId must be longer than 0")
    private long lessonId;

    @NotNull(message = "groupId cannot be null") @Min(value = 1, message = "groupId must be longer than 0")
    private long groupId;

    public static MeetingDto toMeetingDto(Meeting meeting) {
        return new MeetingDto(meeting.getId(), meeting.getDate(), meeting.getLesson().getId(), meeting.getGroup().getId());
    }
}
