package com.alextim.controller.dto;

import com.alextim.domain.MeetingData;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data @NoArgsConstructor @AllArgsConstructor
public class MeetingDataDto {

    public static final String JSON_EXAMPLE ="{\"title\" : \"MeetingData\", \"url\" : \"http://google.com\", \"meetingId\" : 1}";

    @Setter(AccessLevel.NONE)
    private long id;

    @NotNull(message = "title cannot be null") @NotEmpty @NotBlank
    private String title;

    @NotNull(message = "url cannot be null") @NotEmpty @NotBlank
    private String url;

    @NotNull(message = "meetingId cannot be null")  @Min(value = 1, message = "meetingId must be longer than 0")
    private long meetingId;

    public static MeetingDataDto toMeetingDataDto(MeetingData meetingData) {
        return new MeetingDataDto(meetingData.getId(), meetingData.getTitle(), meetingData.getUrl(), meetingData.getMeeting().getId());
    }

}
