package com.alextim.service.working;

import com.alextim.domain.Meeting;
import com.alextim.domain.MeetingData;
import com.alextim.domain.User;
import com.alextim.repository.MeetingDataRepository;
import com.alextim.service.security.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static com.alextim.service.working.HandlerException.EMPTY_RESULT_BY_ID_ERROR_STRING;
import static com.alextim.service.working.HandlerException.handlerException;

@Service
@RequiredArgsConstructor
public class MeetingDataServiceImpl implements MeetingDataService{

    private final MeetingDataRepository meetingDataRepository;

    private final SecurityService securityService;

    private final GroupService groupService;

    @PreAuthorize("hasPermission(#meeting, 'ADMINISTRATION') or hasRole('ROLE_ADMIN')")
    @Override
    public MeetingData add(String title, String url, Meeting meeting) {
        MeetingData meetingData = new MeetingData(title, url, meeting);
        try {
            meetingDataRepository.save(meetingData);
            securityService.addSecurity(SecurityContextHolder.getContext().getAuthentication(),
                    meetingData.getId(),
                    MeetingData.class);
        }
        catch(DataIntegrityViolationException exception) {
            handlerException(exception, meetingData);
        }

        return meetingData;
    }

    @PreAuthorize("hasRole('ROLE_MODERATOR') or hasRole('ROLE_ADMIN')")
    @Override
    public long getCount() {
        return meetingDataRepository.count();
    }

    @PostFilter("hasPermission(returnObject, 'READ') or hasRole('ROLE_ADMIN')")
    @Override
    public List<MeetingData> getAll(int page, int amountByOnePage) {
        return meetingDataRepository.findAll(PageRequest.of(page,amountByOnePage)).getContent();
    }

    @PostAuthorize("hasPermission(returnObject, 'READ') or hasPermission(returnObject, 'ADMINISTRATION') or hasRole('ROLE_MODERATOR') or hasRole('ROLE_ADMIN')")
    @Override
    public MeetingData findById(long id) {
        return meetingDataRepository.findById(id).orElseThrow(()->
                new IllegalArgumentException(String.format(EMPTY_RESULT_BY_ID_ERROR_STRING, MeetingData.class.getSimpleName(), id)));
    }

    @PreAuthorize("hasRole('ROLE_MODERATOR') or hasRole('ROLE_ADMIN')")
    @Override
    public List<MeetingData> find(Meeting meeting) {
        return meetingDataRepository.findByMeeting(meeting);
    }

    @PreAuthorize("hasPermission(#meetingData, 'ADMINISTRATION') or hasRole('ROLE_MODERATOR') or hasRole('ROLE_ADMIN')")
    @Transactional
    @Override
    public MeetingData update(MeetingData meetingData, String title, String url, Meeting meeting) {
        if(title!=null)
            meetingData.setTitle(title);
        if(url!=null)
            meetingData.setUrl(url);
        if(meeting!=null)
            meetingData.setMeeting(meeting);

        try {
            meetingDataRepository.save(meetingData);
        }
        catch(DataIntegrityViolationException exception) {
            handlerException(exception, meetingData);
        }
        return meetingData;
    }

    @PreAuthorize("hasRole('ROLE_MODERATOR') or hasRole('ROLE_ADMIN')")
    @Override
    public void delete(long id) {
        try {
            meetingDataRepository.deleteById(id);
        }
        catch (DataIntegrityViolationException exception) {
            handlerException(exception, null);
        }
    }

    @PreAuthorize("hasRole('ROLE_MODERATOR') or hasRole('ROLE_ADMIN')")
    @Override
    public void deleteAll() {
        try {
            meetingDataRepository.deleteAll();
        }
        catch (DataIntegrityViolationException exception) {
            handlerException(exception, null);
        }
    }
}
