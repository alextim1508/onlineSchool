package com.alextim.controller.working;

import com.alextim.controller.dto.*;
import com.alextim.domain.Group;
import com.alextim.domain.Meeting;
import com.alextim.domain.MeetingsUsers;
import com.alextim.domain.User;
import com.alextim.security.GrantedAuthorityImpl;
import com.alextim.service.working.MeetingService;
import com.alextim.service.working.MeetingsUsersService;
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

import static com.alextim.controller.dto.UserDto.JSON_EXAMPLE;
import static com.alextim.controller.dto.UserRoleActionDto.Action.ADD;
import static com.alextim.controller.dto.UserRoleActionDto.Action.SUB;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_OK;

@RestController @RequestMapping("/user")
@RequiredArgsConstructor @Slf4j
public class UserController {

    private final UserService userService;

    private final MeetingsUsersService meetingsUsersService;

    private final MeetingService meetingService;

    @PostMapping()
    public MessageDto saveUser(@Valid @RequestBody UserDto userDto, BindingResult result,
                               HttpServletRequest request,
                               HttpServletResponse response) {
        if(result.hasErrors()) {
            response.setStatus(SC_BAD_REQUEST);
            return new MessageDto("Error input param. Example: " + JSON_EXAMPLE);
        }

        User user = userService.add(
                userDto.getUsername(),
                userDto.getName(),
                userDto.getSurname(),
                userDto.getEmail(),
                userDto.getRawPassword());

        response.setStatus(SC_OK);
        log.info("{} saved", user);
        return new MessageDto(String.format("%s saved", user));
    }

    @GetMapping("/size")
    public Long getUserCount(HttpServletRequest request,
                             HttpServletResponse response) {
        long count = userService.getCount();
        response.setStatus(SC_OK);
        log.info("User count: {}", count);
        return count;
    }

    @GetMapping()
    public List<UserDto> getUsers(@RequestParam(name = "page", defaultValue = "0") int page,
                                  @RequestParam(name = "amountByOnePage", defaultValue = "100") int amountByOnePage,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        List<User> users = userService.getAll(page, amountByOnePage);
        response.setStatus(SC_OK);
        log.info("Users: {}", users);
        return users.stream().map(UserDto::toUserDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable("id") int id,
                               HttpServletRequest request,
                               HttpServletResponse response) {
        User userById = userService.findById(id);
        response.setStatus(SC_OK);
        log.info("Course: {}", userById);
        return UserDto.toUserDto(userById);
    }

    @GetMapping("/find")
    public List<UserDto> findUsers(@RequestParam(name = "username", defaultValue = "") String username,
                                   @RequestParam(name = "email", defaultValue = "") String email,
                                   @RequestParam(name = "name", defaultValue = "") String name,
                                   @RequestParam(name = "surname", defaultValue = "") String surname,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        List<User> founded = new ArrayList<>();
        if(!username.isEmpty()) {
            founded.add(userService.findByUsername(username));
        }
        if(!email.isEmpty()) {
            founded.add(userService.findByEmail(email));
        }

        response.setStatus(SC_OK);
        log.info("Founded courses: {}", founded);
        return founded.stream().map(UserDto::toUserDto).collect(Collectors.toList());
    }

    @GetMapping("/groups/student/{id}")
    public Set<GroupDto> getStudentGroups(@PathVariable("id") int id,
                                          HttpServletRequest request,
                                          HttpServletResponse response) {
        Set<Group> studentsGroups = userService.getStudentsGroups(id);
        log.info("Founded groups by student id {}: {}", id, studentsGroups);
        return studentsGroups.stream().map(GroupDto::toGroupDto).collect(Collectors.toSet());
    }

    @GetMapping("/groups/teacher/{id}")
    public Set<GroupDto> getTeacherGroups(@PathVariable("id") int id,
                                          HttpServletRequest request,
                                          HttpServletResponse response) {
        Set<Group> teachersGroups = userService.getTeachersGroups(id);
        log.info("Founded groups by teacher id {}: {}", id, teachersGroups);
        return teachersGroups.stream().map(GroupDto::toGroupDto).collect(Collectors.toSet());
    }

    @GetMapping("/groups/meeting/{id}")
    public Set<MeetingDto> getMeetings(@PathVariable("id") int id,
                                       HttpServletRequest request,
                                       HttpServletResponse response) {
        List<Meeting> meetings = userService.getMeeting(id);
        log.info("Founded meetings by student id {}: {}", id, meetings);
        return meetings.stream().map(MeetingDto::toMeetingDto).collect(Collectors.toSet());
    }

    @PostMapping("/role")
    public MessageDto changeRole(@Valid @RequestBody UserRoleActionDto userRoleAction, BindingResult result,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        if(result.hasErrors()) {
            response.setStatus(SC_BAD_REQUEST);
            return new MessageDto("Error input param. Example: " + UserRoleActionDto.JSON_EXAMPLE);
        }

        User userById = userService.findById(userRoleAction.getId());

        GrantedAuthorityImpl.Role[] roles = userRoleAction.getRoles();
        StringBuilder builder = new StringBuilder();
        String splitter = "";
        for(GrantedAuthorityImpl.Role role:  roles) {
            builder.append(splitter).append(role);
            splitter = ", ";
        }

        if(userRoleAction.getAction() == ADD) {
            userService.addRoles(userRoleAction.getId(), roles);
            log.info("{} add to : {}", builder, userById);
            return new MessageDto(String.format("%s add to %s " , builder, userById));
        }
        else if(userRoleAction.getAction() == SUB) {
            userService.removeRoles(userRoleAction.getId(), roles);
            log.info("{} sub to : {}", builder, userById);
            return new MessageDto(String.format("%s sub to %s", builder, userById));
        }
        return new MessageDto(String.format("Unknown action: %s" , userRoleAction.getAction()));
    }

    @PostMapping("/present")
    public MessageDto setPresent(@Valid @RequestBody MeetingsUsersDto meetingsUsersDto,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        User user = userService.findById(meetingsUsersDto.getUserId());
        Meeting meeting = meetingService.findById(meetingsUsersDto.getMeetingId());
        meetingsUsersService.setPresent(meeting, user, meetingsUsersDto.isPresent());
        response.setStatus(SC_OK);
        log.info("{} check to {}", user, meeting);
        return new MessageDto(user + " check to " + meeting);
    }

    @GetMapping("/present/{id}")
    public List<MeetingsUsersDto> getPresent(@PathVariable("id") int id,
                                             HttpServletRequest request,
                                             HttpServletResponse response) {
        User user = userService.findById(id);
        List<MeetingsUsers> meetingUserUser = meetingsUsersService.findByMeetingUserUser(user);
        response.setStatus(SC_OK);
        log.info("MeetingsUsers by user id {}: {}", user.getId(), meetingUserUser);
        return meetingUserUser.stream().map(MeetingsUsersDto::toMeetingsUsersDto).collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public MessageDto updateUser(@PathVariable("id") int id,
                                 @RequestBody UserDto userDto,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        User updated = userService.update(userService.findById(id), userDto.getUsername(), userDto.getName(), userDto.getSurname(), userDto.getEmail(), userDto.getRawPassword());
        response.setStatus(SC_OK);
        log.info("User update: {}", updated);
        return new MessageDto(String.format("User update: %s", updated));
    }

    @DeleteMapping("/{id}")
    public MessageDto deleteUserById(@PathVariable("id") int id,
                                     HttpServletRequest request,
                                     HttpServletResponse response) {
        userService.delete(id);
        response.setStatus(SC_OK);
        log.info("Delete user with id {}", id);
        return new MessageDto(String.format("Delete user with id %d", id));
    }

    @DeleteMapping()
    public MessageDto deleteAllUser(HttpServletRequest request,
                                    HttpServletResponse response) {
        userService.deleteAll();
        response.setStatus(SC_OK);
        log.info("Delete all users");
        return new MessageDto("Delete all users");
    }

    @PostMapping("/block")
    public MessageDto setLock(@Valid @RequestBody LockUserDto lockUserDto, BindingResult result,
                              HttpServletRequest request,
                              HttpServletResponse response) {
        if(result.hasErrors()) {
            response.setStatus(SC_BAD_REQUEST);
            return new MessageDto("Error input param. Example: " + LockUserDto.JSON_EXAMPLE);
        }

        userService.setLock(lockUserDto.getUserId(), lockUserDto.isLock());
        response.setStatus(SC_OK);
        log.info("User with id {} blocked", lockUserDto.getUserId());
        return new MessageDto(String.format("User with id %d blocked", lockUserDto.getUserId()));
    }

}