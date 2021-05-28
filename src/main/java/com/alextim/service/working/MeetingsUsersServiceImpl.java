package com.alextim.service.working;

import com.alextim.domain.Meeting;
import com.alextim.domain.MeetingsUsers;
import com.alextim.domain.User;
import com.alextim.repository.MeetingsUsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.alextim.service.working.HandlerException.handlerException;

@Service
@RequiredArgsConstructor
public class MeetingsUsersServiceImpl implements MeetingsUsersService{

    private final MeetingsUsersRepository meetingsUsersRepository;

    @Transactional
    @Override
    public void setPresent(Meeting meeting, User user, boolean present) {
        List<MeetingsUsers> byUserAndMeeting = meetingsUsersRepository.findByMeetingUserUserAndMeetingUserMeeting(user, meeting);
        byUserAndMeeting.get(0).setPresence(present);
    }

    @Override
    public boolean isPresent(Meeting meeting, User user) {
        List<MeetingsUsers> byUserAndMeeting = meetingsUsersRepository.findByMeetingUserUserAndMeetingUserMeeting(user, meeting);
        return byUserAndMeeting.get(0).isPresence();
    }

    @Override
    public List<MeetingsUsers> findByMeetingUserUser(User user) {
        return  meetingsUsersRepository.findByMeetingUserUser(user);
    }

    @Override
    public void deleteAll() {
        try {
            meetingsUsersRepository.deleteAll();
        }
        catch (DataIntegrityViolationException exception) {
            handlerException(exception, null);
        }
    }
}
