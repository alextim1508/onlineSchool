package com.alextim.controller.working;

import com.alextim.controller.dto.CourseDto;
import com.alextim.controller.dto.MeetingDataDto;
import com.alextim.controller.dto.MessageDto;
import com.alextim.domain.Meeting;
import com.alextim.domain.MeetingData;
import com.alextim.service.working.MeetingDataService;
import com.alextim.service.working.MeetingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.alextim.controller.dto.MeetingDataDto.JSON_EXAMPLE;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_OK;

@RestController @RequestMapping("/meetingData")
@RequiredArgsConstructor @Slf4j
public class MeetingDataController {

    private final MeetingDataService meetingDataService;

    private final MeetingService meetingService;

    @PostMapping()
    public MessageDto saveMeetingData(@Valid @RequestBody MeetingDataDto meetingDataDto, BindingResult result,
                                      HttpServletRequest request,
                                      HttpServletResponse response) {
        if(result.hasErrors()) {
            response.setStatus(SC_BAD_REQUEST);
            return new MessageDto("Error input param. Example: " + JSON_EXAMPLE);
        }

        Meeting meeting = meetingService.findById(meetingDataDto.getMeetingId());
        MeetingData meetingData = meetingDataService.add(meetingDataDto.getTitle(), meetingDataDto.getUrl(), meeting);
        response.setStatus(SC_OK);
        log.info("{} saved", meetingData);
        return new MessageDto(String.format("%s saved", meetingData));
    }


    @GetMapping("/size")
    public Long getMeetingDataCount(HttpServletRequest request,
                               HttpServletResponse response) {
        long count = meetingService.getCount();
        response.setStatus(SC_OK);
        log.info("MeetingService count: {}", count);
        return count;
    }

    @GetMapping()
    public List<MeetingDataDto> getAllMeetingData(  @RequestParam(name = "page", defaultValue = "0") int page,
                                                    @RequestParam(name = "amountByOnePage", defaultValue = "100") int amountByOnePage,
                                                    HttpServletRequest request,
                                                    HttpServletResponse response) {
        List<MeetingData> meetingData = meetingDataService.getAll(page, amountByOnePage);
        response.setStatus(SC_OK);
        log.info("Meeting data: {}", meetingData);
        return meetingData.stream().map(MeetingDataDto::toMeetingDataDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public MeetingDataDto getMeetingDataById(@PathVariable("id") int id,
                                            HttpServletRequest request,
                                            HttpServletResponse response) {
        MeetingData meetingDataById = meetingDataService.findById(id);
        response.setStatus(SC_OK);
        log.info("Meeting data: {}", meetingDataById);
        return MeetingDataDto.toMeetingDataDto(meetingDataById);
    }

    @GetMapping("/find")
    public List<MeetingDataDto> findMeetingData(@RequestParam(name = "meetingId", defaultValue = "-1") int meetingId,
                                       HttpServletRequest request,
                                       HttpServletResponse response) {
        List<MeetingData> founded = new ArrayList<>();

        if(meetingId != -1) {
            founded.addAll(meetingDataService.find(meetingService.findById(meetingId)));
        }
        response.setStatus(SC_OK);
        log.info("Founded lessons: {}", founded);
        return founded.stream().map(MeetingDataDto::toMeetingDataDto).collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public MessageDto updateMeetingData(@PathVariable("id") int id,
                                   @RequestBody MeetingDataDto meetingDataDto,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
         Meeting meeting = meetingService.findById(meetingDataDto.getMeetingId());
        MeetingData updated = meetingDataService.update(meetingDataService.findById(id), meetingDataDto.getTitle(), meetingDataDto.getUrl(), meeting);
        response.setStatus(SC_OK);
        log.info("Meeting data update: {}", updated);
        return new MessageDto(String.format("Meeting data update: %s", updated));
    }

    @DeleteMapping("/{id}")
    public MessageDto deleteMeetingDataById(@PathVariable("id") int id,
                                       HttpServletRequest request,
                                       HttpServletResponse response) {
        meetingDataService.delete(id);
        response.setStatus(SC_OK);
        log.info("Delete meeting data with id {}", id);
        return new MessageDto("Delete meeting data with id " + id);
    }

    @DeleteMapping()
    public MessageDto deleteAllMeetingData( HttpServletRequest request,
                                            HttpServletResponse response) {
        meetingDataService.deleteAll();
        response.setStatus(SC_OK);
        log.info("Delete all meeting data");
        return new MessageDto("Delete all meeting data");
    }
}
