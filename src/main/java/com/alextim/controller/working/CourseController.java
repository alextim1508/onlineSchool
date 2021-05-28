package com.alextim.controller.working;

import com.alextim.controller.dto.CourseDto;
import com.alextim.controller.dto.GroupDto;
import com.alextim.controller.dto.LessonDto;
import com.alextim.controller.dto.MessageDto;
import com.alextim.domain.Course;
import com.alextim.domain.Group;
import com.alextim.domain.Lesson;
import com.alextim.service.working.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
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

@RestController @RequestMapping("/course")
@RequiredArgsConstructor @Slf4j
public class CourseController {

    private final CourseService courseService;

    @Transactional
    @PostMapping()
    public MessageDto saveCourse(@Valid @RequestBody CourseDto courseDto, BindingResult result,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        if(result.hasErrors()) {
            response.setStatus(SC_BAD_REQUEST);
            return new MessageDto("Error input param. Example: " + JSON_EXAMPLE);
        }

        Course course = courseService.add(courseDto.getTitle(), courseDto.getDescription());
        response.setStatus(SC_OK);
        log.info("{} saved", course);
        return new MessageDto(String.format("%s saved", course));
    }

    @GetMapping("/size")
    public long getCourseCount(HttpServletRequest request,
                               HttpServletResponse response) {
        long count = courseService.getCount();
        response.setStatus(SC_OK);
        log.info("Course count: {}", count);
        return count;
    }

    @GetMapping()
    public List<CourseDto> getAllCourses(@RequestParam(name = "page", defaultValue = "0") int page,
                                         @RequestParam(name = "amountByOnePage", defaultValue = "100") int amountByOnePage,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        List<Course> courses = courseService.getAll(page, amountByOnePage);
        response.setStatus(SC_OK);
        log.info("Courses: {}", courses);
        return courses.stream().map(CourseDto::toCourseDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public CourseDto getCourseById(@PathVariable("id") int id,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        Course courseById = courseService.findById(id);
        response.setStatus(SC_OK);
        log.info("Course: {}", courseById);
        return CourseDto.toCourseDto(courseById);
    }

    @GetMapping("/find")
    public List<CourseDto> findCourses(@RequestParam(name = "title", defaultValue = "") String title,
                                       @RequestParam(name = "subDescription", defaultValue = "") String subDescription,
                                       HttpServletRequest request,
                                       HttpServletResponse response) {
        List<Course> founded = new ArrayList<>();
        if(!title.isEmpty()) {
            founded.addAll(courseService.findByTitle(title));
        }
        if(!subDescription.isEmpty()) {
            founded.addAll(courseService.findBySubDescription(subDescription));
        }
        response.setStatus(SC_OK);
        log.info("Founded courses: {}", founded);
        return founded.stream().map(CourseDto::toCourseDto).collect(Collectors.toList());
    }

    @GetMapping("/groups/{id}")
    public List<GroupDto> getGroups(@PathVariable("id") int id,
                                    HttpServletRequest request,
                                    HttpServletResponse response) {
        List<Group> groups = courseService.getGroup(id);
        response.setStatus(SC_OK);
        log.info("Founded groups by course with id {}: {}", id, groups);
        return groups.stream().map(GroupDto::toGroupDto).collect(Collectors.toList());
    }

    @GetMapping("/lesson/{id}")
    public List<LessonDto> getLessons(@PathVariable("id") int id,
                                      HttpServletRequest request,
                                      HttpServletResponse response) {
        List<Lesson> lessons = courseService.getLesson(id);
        response.setStatus(SC_OK);
        log.info("Founded lessons by course id {}: {}", id, lessons);
        return lessons.stream().map(LessonDto::toLessonDto).collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public MessageDto updateCourse(@PathVariable("id") int id,
                                   @RequestBody CourseDto courseDto,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        Course updated = courseService.update(courseService.findById(id), courseDto.getTitle(), courseDto.getDescription());
        response.setStatus(SC_OK);
        log.info("Course update: {}", updated);
        return new MessageDto(String.format("Course update: %s", updated));
    }

    @DeleteMapping("/{id}")
    public MessageDto deleteCourseById(@PathVariable("id") int id,
                                       HttpServletRequest request,
                                       HttpServletResponse response) {
        courseService.delete(id);
        response.setStatus(SC_OK);
        log.info("Delete course with id {}", id);
        return new MessageDto(String.format("Delete course with id %d", id));
    }

    @DeleteMapping()
    public MessageDto deleteAllCourses(HttpServletRequest request,
                                       HttpServletResponse response) {
        courseService.deleteAll();
        response.setStatus(SC_OK);
        log.info("Delete all courses");
        return new MessageDto("Delete all courses");
    }

}