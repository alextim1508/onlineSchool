package com.alextim.controller.working;

import com.alextim.controller.dto.*;
import com.alextim.domain.Course;
import com.alextim.domain.Group;
import com.alextim.domain.Meeting;
import com.alextim.domain.User;
import com.alextim.service.working.CourseService;
import com.alextim.service.working.GroupService;
import com.alextim.service.working.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.alextim.controller.dto.CourseDto.JSON_EXAMPLE;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_OK;

@RestController @RequestMapping("/group")
@RequiredArgsConstructor @Slf4j
public class GroupController {

    private final UserService userService;

    private final GroupService groupService;

    private final CourseService courseService;

    @PostMapping()
    public MessageDto saveGroup(@Valid @RequestBody GroupDto groupDto,  BindingResult result,
                                HttpServletRequest request,
                                HttpServletResponse response) {
        if(result.hasErrors()) {
            response.setStatus(SC_BAD_REQUEST);
            return new MessageDto("Error input param. Example: " + JSON_EXAMPLE);
        }

        Course course = courseService.findById(groupDto.getCourseId());
        Group group = groupService.add(groupDto.getTitle(), course);
        response.setStatus(SC_OK);
        log.info("{} saved", group);
        return new MessageDto(group + " saved");
    }

    @GetMapping("/size")
    public Long getGroupCount(HttpServletRequest request,
                              HttpServletResponse response) {
        long count = groupService.getCount();
        response.setStatus(SC_OK);
        log.info("Group count: {}", count);
        return count;
    }

    @GetMapping()
    public List<GroupDto> getAllGroup(@RequestParam(name = "page", defaultValue = "0") int page,
                                      @RequestParam(name = "amountByOnePage", defaultValue = "100") int amountByOnePage,
                                      HttpServletRequest request,
                                      HttpServletResponse response) {
        List<Group> groups = groupService.getAll(page, amountByOnePage);
        response.setStatus(SC_OK);
        log.info("Group: {}", groups);
        return groups.stream().map(GroupDto::toGroupDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public GroupDto getGroupById(@PathVariable("id") int id,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        Group groupById = groupService.findById(id);
        response.setStatus(SC_OK);
        log.info("Group: {}", groupById);
        return GroupDto.toGroupDto(groupById);
    }

    @PostMapping("/status")
    public MessageDto setGroupStatus(@Valid @RequestBody StatusGroupDto statusGroupDto, BindingResult result,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        if(result.hasErrors()) {
            response.setStatus(SC_BAD_REQUEST);
            return new MessageDto("Error input param. Example: " + StatusGroupDto.JSON_EXAMPLE);
        }

        groupService.setStatus(statusGroupDto.getGroupId(), Group.Status.valueOf(statusGroupDto.getStatus()));
        response.setStatus(SC_OK);
        log.info("Status group {}: {}",statusGroupDto.getGroupId(), Group.Status.valueOf(statusGroupDto.getStatus()));
        return new MessageDto(String.format("Status group %d: %s", statusGroupDto.getGroupId(), Group.Status.valueOf(statusGroupDto.getStatus())));
    }

    @GetMapping("/find")
    public List<GroupDto> findGroup(@RequestParam(name = "title", defaultValue = "") String title,
                                    @RequestParam(name = "courseId", defaultValue = "-1") int courseId,
                                    HttpServletRequest request,
                                    HttpServletResponse response) {
        List<Group> founded = new ArrayList<>();
        if(!title.isEmpty()) {
            founded.addAll(groupService.findByTitle(title));
        }
        if(courseId != -1) {
            founded.addAll(groupService.findByCourse(courseService.findById(courseId)));
        }
        response.setStatus(SC_OK);
        log.info("Founded groups: {}", founded);
        return founded.stream().map(GroupDto::toGroupDto).collect(Collectors.toList());
    }

    @GetMapping("/meetings/{id}")
    public List<MeetingDto> getMeetings(@PathVariable("id") int id,
                                        HttpServletRequest request,
                                        HttpServletResponse response) {
        List<Meeting> meetings = groupService.getMeetings(id);
        response.setStatus(SC_OK);
        log.info("Founded meetings by group id: {}", meetings);
        return meetings.stream().map(MeetingDto::toMeetingDto).collect(Collectors.toList());
    }

    @PostMapping("/student/{id}")
    public MessageDto addStudentToGroup(@PathVariable("id") int id,
                                        @Valid @RequestBody UserListActionDto userListActionDto, BindingResult result,
                                        HttpServletRequest request,
                                        HttpServletResponse response) {
        if(result.hasErrors()) {
            response.setStatus(SC_BAD_REQUEST);
            return new MessageDto("Error input param. Example: " + UserListActionDto.JSON_EXAMPLE);
        }

        List<User> usersById = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        String splitter = "";
        for(int userId: userListActionDto.getUsersId()) {
            User byId = userService.findById(userId);
            usersById.add(byId);
            builder.append(splitter).append(byId.getName());
            splitter = " ,";
        }
        Group groupById = groupService.findById(id);
        if(UserListActionDto.Action.valueOf(userListActionDto.getAction()).equals(UserListActionDto.Action.ADD)) {
            groupService.addStudent(groupById, usersById);
            response.setStatus(SC_OK);
            log.info("Student {} added to {}", builder, groupById);
            return new MessageDto(String.format("Student %s add to %s", builder, groupById));
        }
        else {
            groupService.subStudent(groupById, usersById);
            response.setStatus(SC_OK);
            log.info("Student {} added to {}", builder, groupById);
            return new MessageDto(String.format("Student %s sub from %s", builder, groupById));
        }
    }

    @GetMapping("/student/{id}")
    public List<UserDto> getGroupStudents(@PathVariable("id") int id,
                                        HttpServletRequest request,
                                        HttpServletResponse response) {
        Group groupById = groupService.findById(id);
        Set<User> learners = groupService.getStudent(groupById);

        response.setStatus(SC_OK);
        log.info("Students {} by group {}", learners, groupById);
        return learners.stream().map(UserDto::toUserDto).collect(Collectors.toList());
    }

    @PostMapping("/teacher/{id}")
    public MessageDto addTeacherToGroup(@PathVariable("id") int id,
                                        @Valid @RequestBody UserListActionDto userListActionDto, BindingResult result,
                                        HttpServletRequest request,
                                        HttpServletResponse response) {
        if(result.hasErrors()) {
            response.setStatus(SC_BAD_REQUEST);
            return new MessageDto("Error input param. Example: " + UserListActionDto.JSON_EXAMPLE);
        }

        List<User> usersById = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        String splitter ="";
        for(int userId: userListActionDto.getUsersId()) {
            User byId = userService.findById(userId);
            usersById.add(byId);
            builder.append(splitter).append(byId.getName());
            splitter = " ,";
        }
        Group groupById = groupService.findById(id);
        if(UserListActionDto.Action.valueOf(userListActionDto.getAction()).equals(UserListActionDto.Action.ADD)) {
            groupService.addTeacher(groupById, usersById);
            response.setStatus(SC_OK);
            log.info("Teachers {} added to {}",  builder.toString(), groupById);
            return new MessageDto(String.format("Teachers %s add to %s", builder.toString(), groupById));
        }
        else {
            groupService.subTeacher(groupById, usersById);
            response.setStatus(SC_OK);
            log.info("Teachers {} added to {}",  builder.toString(), groupById);
            return new MessageDto(String.format("Teachers %s sub from %s", builder.toString(), groupById));
        }

    }

    @GetMapping("/teacher/{id}")
    public List<UserDto> getGroupTeachers(@PathVariable("id") int id,
                                           HttpServletRequest request,
                                           HttpServletResponse response) {
        Group groupById = groupService.findById(id);
        Set<User> learners = groupService.getTeachers(groupById);

        response.setStatus(SC_OK);
        log.info("Teachers {} by group {}", learners, groupById);
        return learners.stream().map(UserDto::toUserDto).collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public MessageDto updateGroup(@PathVariable("id") int id,
                                  @RequestBody GroupDto groupDto,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        Course course = courseService.findById(groupDto.getCourseId());
        Group updated = groupService.update(groupService.findById(id), groupDto.getTitle(), course);
        response.setStatus(SC_OK);
        log.info("Group update: {}", updated);
        return new MessageDto(String.format("Group update: %s", updated));
    }

    @DeleteMapping("/{id}")
    public MessageDto deleteGroupById(@PathVariable("id") int id,
                                       HttpServletRequest request,
                                       HttpServletResponse response) {
        groupService.delete(id);
        response.setStatus(SC_OK);
        log.info("Delete lesson with id {}", id);
        return new MessageDto(String.format("Delete group with id %d", id));
    }

    @DeleteMapping()
    public MessageDto deleteAllGroups(HttpServletRequest request,
                                       HttpServletResponse response) {
        groupService.deleteAll();
        response.setStatus(SC_OK);
        log.info("Delete all lessons");
        return new MessageDto("Delete all groups");
    }

}
