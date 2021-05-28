package com.alextim.controller.working;

import com.alextim.controller.dto.LessonDto;
import com.alextim.controller.dto.MeetingDto;
import com.alextim.controller.dto.MessageDto;
import com.alextim.domain.Course;
import com.alextim.domain.Lesson;
import com.alextim.domain.Meeting;
import com.alextim.service.working.CourseService;
import com.alextim.service.working.LessonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.alextim.controller.dto.CourseDto.JSON_EXAMPLE;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_OK;

@RestController @RequestMapping("/lesson")
@RequiredArgsConstructor @Slf4j
public class LessonController {

    private final LessonService lessonService;

    private final CourseService courseService;

    @PostMapping()
    public MessageDto saveLesson(@Valid @RequestBody LessonDto lessonDto, BindingResult result,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        if(result.hasErrors()) {
            response.setStatus(SC_BAD_REQUEST);
            return new MessageDto("Error input param. Example: " + JSON_EXAMPLE);
        }

        Course course = courseService.findById(lessonDto.getCourseId());
        Lesson lesson = lessonService.add(lessonDto.getTitle(), lessonDto.getHomework(), course);
        response.setStatus(SC_OK);
        log.info("{} saved", lesson);
        return new MessageDto(String.format("%s saved", lesson));
    }

    @GetMapping("/size")
    public long getLessonCount(HttpServletRequest request,
                               HttpServletResponse response) {
        long count = lessonService.getCount();
        response.setStatus(SC_OK);
        log.info("Lesson count: {}", count);
        return count;
    }

    @GetMapping()
    public List<LessonDto> getAllLesson(@RequestParam(name = "page", defaultValue = "0") int page,
                                        @RequestParam(name = "amountByOnePage", defaultValue = "100") int amountByOnePage,
                                        HttpServletRequest request,
                                        HttpServletResponse response) {
        List<Lesson> lessons = lessonService.getAll(page, amountByOnePage);
        response.setStatus(SC_OK);
        log.info("Lesson: {}", lessons);
        return lessons.stream().map(LessonDto::toLessonDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public LessonDto getLessonById(@PathVariable("id") int id,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        Lesson lessonById = lessonService.findById(id);
        response.setStatus(SC_OK);
        log.info("Lesson: {}", lessonById);
        return LessonDto.toLessonDto(lessonById);
    }

    @GetMapping("/find")
    public List<LessonDto> findLessons(@RequestParam(name = "title", defaultValue = "") String title,
                                       @RequestParam(name = "subHomework", defaultValue = "") String subHomework,
                                       @RequestParam(name = "courseId", defaultValue = "-1") long courseId,
                                       HttpServletRequest request,
                                       HttpServletResponse response) {
        List<Lesson> founded = new ArrayList<>();
        if(!title.isEmpty()) {
            founded.addAll(lessonService.findByTitle(title));
        }
        if(!subHomework.isEmpty()) {
            founded.addAll(lessonService.findByHomework(subHomework));
        }
        if(courseId != -1) {
            founded.addAll(lessonService.findByCourse(courseService.findById(courseId)));
        }
        response.setStatus(SC_OK);
        log.info("Founded lessons: {}", founded);
        return founded.stream().map(LessonDto::toLessonDto).collect(Collectors.toList());
    }

    @GetMapping("/meetings/{id}")
    public List<MeetingDto> getMeetings(@PathVariable("id") int id,
                                        HttpServletRequest request,
                                        HttpServletResponse response) {
        List<Meeting> meetings = lessonService.getMeetings(id);
        response.setStatus(SC_OK);
        log.info("Founded meetings by course id: {}", meetings);
        return meetings.stream().map(MeetingDto::toMeetingDto).collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public MessageDto updateLesson(@PathVariable("id") int id,
                                   @RequestBody LessonDto lessonDto,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        Course course = courseService.findById(lessonDto.getCourseId());
        Lesson updated = lessonService.update(lessonService.findById(id), lessonDto.getTitle(), lessonDto.getHomework(), course);
        response.setStatus(SC_OK);
        log.info("Lesson update: {}", updated);
        return new MessageDto(String.format("Lesson update: %s", updated));
    }

    @DeleteMapping("/{id}")
    public MessageDto deleteLessonById(@PathVariable("id") int id,
                                       HttpServletRequest request,
                                       HttpServletResponse response) {
        lessonService.delete(id);
        response.setStatus(SC_OK);
        log.info("Delete lesson with id {}", id);
        return new MessageDto(String.format("Delete lesson with id %d", id));
    }

    @DeleteMapping()
    public MessageDto deleteAllLessons(HttpServletRequest request,
                                       HttpServletResponse response) {
        lessonService.deleteAll();
        response.setStatus(SC_OK);
        log.info("Delete all lessons");
        return new MessageDto("Delete all lessons");
    }
}