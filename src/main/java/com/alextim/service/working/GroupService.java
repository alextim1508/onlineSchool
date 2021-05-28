package com.alextim.service.working;

import com.alextim.domain.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

public interface GroupService {
    Group add(String title, Course course);

    void setStatus(long id, Group.Status status);

    void addTeacher(Group group, List<User> user);
    void subTeacher(Group group, List<User> user);
    Set<User> getTeachers(Group group);

    void addStudent(Group group, List<User> user);
    void subStudent(Group group, List<User> user);
    Set<User> getStudent(Group group);

    long getCount();
    List<Group> getAll(int page, int amountByOnePage);

    Group findById(long id);
    List<Group> findByTitle(String title);
    List<Group> findByCourse(Course course);

    List<Meeting> getMeetings(long id);

    Group update(Group group, String title, Course course);

    void delete(long id);
    void deleteAll();
}
