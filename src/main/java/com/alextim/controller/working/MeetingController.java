package com.alextim.controller.working;

import com.alextim.controller.dto.MeetingDto;
import com.alextim.controller.dto.MessageDto;
import com.alextim.domain.Group;
import com.alextim.domain.Lesson;
import com.alextim.domain.Meeting;
import com.alextim.service.security.SecurityService;
import com.alextim.service.working.GroupService;
import com.alextim.service.working.LessonService;
import com.alextim.service.working.MeetingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.alextim.controller.dto.MeetingDto.JSON_EXAMPLE;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_OK;

@RestController @RequestMapping("/meeting")
@RequiredArgsConstructor @Slf4j
public class MeetingController {

    private final GroupService groupService;

    private final LessonService lessonService;

    private final MeetingService meetingService;

    private final SecurityService securityService;

    @PostMapping()
    public MessageDto saveMeeting(@Valid @RequestBody MeetingDto meetingDto, BindingResult result,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        if(result.hasErrors()) {
            response.setStatus(SC_BAD_REQUEST);
            return new MessageDto("Error input param. Example: " + JSON_EXAMPLE);
        }

        Lesson lesson = lessonService.findById(meetingDto.getLessonId());
        Group group = groupService.findById(meetingDto.getGroupId());
        Meeting meeting = meetingService.add(meetingDto.getDate(), lesson, group);

        response.setStatus(SC_OK);
        log.info("{} saved", meeting);
        return new MessageDto(meeting + " saved");
    }

    @GetMapping("/size")
    public Long getMeeting(HttpServletRequest request,
                                HttpServletResponse response) {
        long count = meetingService.getCount();
        response.setStatus(SC_OK);
        log.info("Meeting count: {}", count);
        return count;
    }

    @GetMapping()
    public List<MeetingDto> getAllMeeting(@RequestParam(name = "page", defaultValue = "0") int page,
                                      @RequestParam(name = "amountByOnePage", defaultValue = "100") int amountByOnePage,
                                      HttpServletRequest request,
                                      HttpServletResponse response) {
        List<Meeting> meetings = meetingService.getAll(page, amountByOnePage);
        response.setStatus(SC_OK);
        log.info("Meetings: {}", meetings);
        return meetings.stream().map(MeetingDto::toMeetingDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public MeetingDto getMeetingById(@PathVariable("id") int id,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        Meeting meetingById = meetingService.findById(id);
        response.setStatus(SC_OK);
        log.info("Meeting: {}", meetingById);
        return MeetingDto.toMeetingDto(meetingById);
    }

    @GetMapping("/find")
    public List<MeetingDto> findMeeting(@RequestParam(name = "date1", defaultValue = "0000-00-00 00:00:00") Date date1,
                                        @RequestParam(name = "date2", defaultValue = "0000-00-00 00:00:00") Date date2,
                                        @RequestParam(name = "lessonId", defaultValue = "-1") int lessonId,
                                        @RequestParam(name = "groupId", defaultValue = "-1") int groupId,
                                        HttpServletRequest request,
                                        HttpServletResponse response) {
        List<Meeting> founded = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);
        if(cal.get(Calendar.YEAR) != 0) {
            founded.addAll(meetingService.findByBetweenDate(date1, date2));
        }
        if(groupId != -1) {
            founded.addAll(meetingService.findByGroup(groupService.findById(groupId)));
        }
        if(lessonId != -1) {
            founded.addAll(meetingService.findByLesson(lessonService.findById(lessonId)));
        }

        response.setStatus(SC_OK);
        log.info("Founded groups: {}", founded);
        return founded.stream().map(MeetingDto::toMeetingDto).collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public MessageDto updateMeeting(@PathVariable("id") int id,
                                   @RequestBody MeetingDto meetingDtoDto,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
         Lesson lesson = lessonService.findById(meetingDtoDto.getLessonId());
        Group group = groupService.findById(meetingDtoDto.getGroupId());
        Meeting updated = meetingService.update(meetingService.findById(id), meetingDtoDto.getDate(), lesson, group);
        response.setStatus(SC_OK);
        log.info("Meeting update: {}", updated);
        return new MessageDto("Meeting update: " + updated);
    }

    @DeleteMapping("/{id}")
    public MessageDto deleteMeetingById(@PathVariable("id") int id,
                                       HttpServletRequest request,
                                       HttpServletResponse response) {
        meetingService.delete(id);
        response.setStatus(SC_OK);
        log.info("Delete meeting with id {}", id);
        return new MessageDto("Delete meeting with id " + id);
    }

    @DeleteMapping()
    public MessageDto deleteAllMeetings(HttpServletRequest request,
                                       HttpServletResponse response) {
        meetingService.deleteAll();
        response.setStatus(SC_OK);
        log.info("Delete all meetings");
        return new MessageDto("Delete all meetings");
    }
}
