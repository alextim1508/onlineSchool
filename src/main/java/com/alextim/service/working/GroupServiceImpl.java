package com.alextim.service.working;

import com.alextim.domain.Course;
import com.alextim.domain.Group;
import com.alextim.domain.Meeting;
import com.alextim.domain.User;
import com.alextim.repository.GroupRepository;
import com.alextim.service.security.SecurityService;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static com.alextim.service.working.HandlerException.EMPTY_RESULT_BY_ID_ERROR_STRING;
import static com.alextim.service.working.HandlerException.handlerException;
import static org.springframework.security.acls.domain.BasePermission.ADMINISTRATION;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService  {

    private final GroupRepository groupRepository;

    private final SecurityService securityService;

    @Override
    public Group add(String title, Course course) {
        Group group = new Group(title, course);
        try {
            group = groupRepository.save(group);
            securityService.addSecurity(SecurityContextHolder.getContext().getAuthentication(),
                    group.getId(),
                    Group.class);
        }
        catch (DataIntegrityViolationException exception) {
            handlerException(exception, group);
        }
        return group;
    }


    @Transactional
    @Override
    public void setStatus(long id, Group.Status status) {
        Group byId = findById(id);
        byId.setStatus(status);
    }


    @Transactional
    @Override
    public void addTeacher(Group group, List<User> user) {
        Group groupById = findById(group.getId());

        user.forEach(currentUser -> {
            groupById.getTeachers().add(currentUser);
            securityService.addPermission(group.getId(), Group.class, currentUser.getUsername(), BasePermission.READ);
        });
    }

    @Transactional
    @Override
    public void subTeacher(Group group, List<User> user) {
        Group groupById = findById(group.getId());
        groupById.getTeachers().removeAll(user);
    }

    @Transactional
    @Override
    public Set<User> getTeachers(Group group) {
        Group groupById = findById(group.getId());
        Set<User> teachers = groupById.getTeachers();
        int forEagerLoading = teachers.size();
        return teachers;
    }

    @Transactional
    @Override
    public void addStudent(Group group, List<User> user) {
        Group groupById = findById(group.getId());

        user.forEach(currentUser -> {
            groupById.getStudents().add(currentUser);
            securityService.addPermission(group.getId(), Group.class, currentUser.getUsername(), BasePermission.READ);
        });
    }

    @Transactional
    @Override
    public void subStudent(Group group, List<User> user) {
        Group groupById = findById(group.getId());
        groupById.getStudents().removeAll(user);
    }

    @Transactional
    @Override
    public Set<User> getStudent(Group group) {
        Group groupById = findById(group.getId());
        Set<User> students = groupById.getStudents();
        int forEagerLoading = students.size();
        return students;
    }

    @Override
    public long getCount() {
        return groupRepository.count();
    }

    @Override
    public List<Group> getAll(int page, int amountByOnePage) {
        return groupRepository.findAll(PageRequest.of(page,amountByOnePage)).getContent();
    }

    @Transactional
    @Override
    public Group findById(long id) {
        return groupRepository.findById(id).orElseThrow(()->
                new IllegalArgumentException(String.format(EMPTY_RESULT_BY_ID_ERROR_STRING, Group.class.getSimpleName(), id)));
    }

    @Override
    public List<Group> findByCourse(Course course) {
        return groupRepository.findByCourse(course);
    }

    @Override
    public List<Group> findByTitle(String title) {
        return groupRepository.findByTitle(title);
    }

    @Transactional
    @Override
    public List<Meeting> getMeetings(long id) {
        Group groupById = findById(id);
        List<Meeting> meetings = groupById.getMeetings();
        int forEagerLoading = meetings.size();
        return meetings;
    }

    @Transactional
    @Override
    public Group update(Group group, String title, Course course) {
        if(title!= null)
            group.setTitle(title);
        if(course!=null)
            group.setCourse(course);

        try {
            group = groupRepository.save(group);
        }
        catch (DataIntegrityViolationException exception) {
            handlerException(exception, group);
        }
        return group;
    }

    @Override
    public void delete(long id) {
        try {
            groupRepository.deleteById(id);
        }
        catch (DataIntegrityViolationException exception) {
            handlerException(exception, null);
        }
    }

    @Override
    public void deleteAll() {
        try {
            groupRepository.deleteAll();
        }
        catch (DataIntegrityViolationException exception) {
            handlerException(exception, null);
        }
    }
}
