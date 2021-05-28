package com.alextim.service.working;

import com.alextim.domain.*;
import com.alextim.repository.MeetingRepository;
import com.alextim.service.security.SecurityService;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.alextim.service.working.HandlerException.EMPTY_RESULT_BY_ID_ERROR_STRING;
import static com.alextim.service.working.HandlerException.handlerException;

@Service
@RequiredArgsConstructor
public class MeetingServiceImpl implements MeetingService {

    private final MeetingRepository meetingRepository;

    private final GroupService groupService;

    private final SecurityService securityService;

    @PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_ADMIN')")
    @Override
    public Meeting add(Date date, Lesson lesson, Group group) {
        Meeting meeting = new Meeting(date, lesson, group);

        Set<User> students = groupService.getStudent(group);

        if( students!= null) {
            meeting.setMeetingsUsers(
                    students.stream().map(user -> new MeetingsUsers(new MeetingsUsers.MeetingUser(user, meeting), false)).collect(Collectors.toList()));
        }

        try {
            meetingRepository.save(meeting);
            securityService.addSecurity(SecurityContextHolder.getContext().getAuthentication(),
                    meeting.getId(),
                    Meeting.class);
        }
        catch (DataIntegrityViolationException exception) {
            handlerException(exception, meeting);
        }

        if (students != null) {
            students.forEach(user -> securityService.addPermission(meeting.getId(), Meeting.class, user.getUsername(), BasePermission.READ));
        }

        return meeting;
    }

    @Override
    public long getCount() {
        return meetingRepository.count();
    }

    @PostFilter("hasPermission(returnObject, 'READ') or hasRole('ROLE_MODERATOR') or hasRole('ROLE_ADMIN')")
    @Override
    public List<Meeting> getAll(int page, int amountByOnePage) {
        return meetingRepository.findAll(PageRequest.of(page,amountByOnePage)).getContent();
    }

    @PostAuthorize("hasPermission(returnObject, 'ADMINISTRATION') or hasRole('ROLE_ADMIN')")
    @Transactional
    @Override
    public Meeting findById(long id) {
        return meetingRepository.findById(id).orElseThrow(()->
                new IllegalArgumentException(String.format(EMPTY_RESULT_BY_ID_ERROR_STRING, Course.class.getSimpleName(), id)));
    }

    @Override
    public List<Meeting> findByBetweenDate(Date date1, Date date2) {
        return meetingRepository.findByDateBetween(date1, date2);
    }

    @Override
    public List<Meeting> findByLesson(Lesson lesson) {
        return meetingRepository.findByLesson(lesson);
    }

    @Override
    public List<Meeting> findByGroup(Group group) {
        return meetingRepository.findByGroup(group);
    }

    @Override
    public List<MeetingData> getMeetingData(long id) {
        Meeting meetingById = findById(id);
        List<MeetingData> meetingData = meetingById.getMeetingData();
        Hibernate.initialize(meetingData);
        return meetingData;
    }

    @PreAuthorize("hasPermission(#meeting, 'ADMINISTRATION') or hasRole('ROLE_ADMIN')")
    @Transactional
    @Override
    public Meeting update(Meeting meeting, Date date, Lesson lesson, Group group) {
        if(date!= null)
            meeting.setDate(date);
        if(lesson!=null)
            meeting.setLesson(lesson);
        if(group!=null)
            meeting.setGroup(group);

        try {
            meeting = meetingRepository.save(meeting);
        }
        catch (DataIntegrityViolationException exception) {
            handlerException(exception, meeting);
        }
        return meeting;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Override
    public void delete(long id) {
        try {
            meetingRepository.deleteById(id);
        }
        catch (DataIntegrityViolationException exception) {
            handlerException(exception, null);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Override
    public void deleteAll() {
        try {
            meetingRepository.deleteAll();
        }
        catch (DataIntegrityViolationException exception) {
            handlerException(exception, null);
        }
    }
}
