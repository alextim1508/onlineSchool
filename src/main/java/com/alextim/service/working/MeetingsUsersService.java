package com.alextim.service.working;

import com.alextim.domain.Meeting;
import com.alextim.domain.MeetingsUsers;
import com.alextim.domain.User;

import java.util.List;

public interface MeetingsUsersService {
    void setPresent(Meeting meeting, User user, boolean present);
    boolean isPresent(Meeting meeting, User user);

    List<MeetingsUsers> findByMeetingUserUser(User user);

    void deleteAll();
}
