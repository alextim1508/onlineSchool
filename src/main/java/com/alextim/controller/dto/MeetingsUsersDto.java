package com.alextim.controller.dto;

import com.alextim.domain.MeetingsUsers;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;


@Data @NoArgsConstructor @AllArgsConstructor
public class MeetingsUsersDto {

    @Min(value = 0, message = "userId must be longer than 0") @NotNull(message = "userId cannot be null")
    private long userId;

    @Min(value = 0,message = "meetingId must be longer than 0") @NotNull(message = "meetingId cannot be null")
    private long meetingId;

    @Min(value = 0,message = "present must be longer than 0") @NotNull(message = "present cannot be null")
    private boolean present;

    public static MeetingsUsersDto toMeetingsUsersDto(MeetingsUsers meetingsUsers) {
        return new MeetingsUsersDto(
                meetingsUsers.getMeetingUser().getUser().getId(),
                meetingsUsers.getMeetingUser().getMeeting().getId(),
                meetingsUsers.isPresence());
    }
}
