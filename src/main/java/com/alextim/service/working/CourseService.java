package com.alextim.service.working;

import com.alextim.domain.Course;
import com.alextim.domain.Group;
import com.alextim.domain.Lesson;

import java.util.List;

public interface CourseService {
    Course add(String title, String description);

    long getCount();
    List<Course> getAll(int page, int amountByOnePage);

    Course findById(long id);
    List<Course> findByTitle(String title);
    List<Course> findBySubDescription(String subDescription);

    List<Lesson> getLesson(long id);
    List<Group> getGroup(long id);

    Course update(Course course, String title, String description);

    void delete(long id);
    void deleteAll();
}
