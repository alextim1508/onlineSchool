package com.alextim.service.working;

import com.alextim.domain.Course;
import com.alextim.domain.Lesson;
import com.alextim.domain.Meeting;

import java.util.List;

public interface LessonService {
    Lesson add(String title, String homework, Course course);

    long getCount();
    List<Lesson> getAll(int page, int amountByOnePage);

    Lesson findById(long id);
    List<Lesson> findByTitle(String title);
    List<Lesson> findByHomework(String subHomework);
    List<Lesson> findByCourse(Course course);

    List<Meeting> getMeetings(long id);

    Lesson update(Lesson lesson, String title, String homework, Course course);
    void delete(long id);
    void deleteAll();
}
