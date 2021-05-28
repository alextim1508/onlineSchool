package com.alextim.repository;

import com.alextim.domain.Meeting;
import com.alextim.domain.MeetingsUsers;
import com.alextim.domain.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingsUsersRepository extends PagingAndSortingRepository<MeetingsUsers, Long> {

    List<MeetingsUsers> findByMeetingUserUserAndMeetingUserMeeting(User user, Meeting meeting);
    List<MeetingsUsers> findByMeetingUserUser(User user);
}
