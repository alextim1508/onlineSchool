package com.alextim.repository;

import com.alextim.domain.Course;
import com.alextim.domain.Lesson;
import com.alextim.domain.Meeting;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends PagingAndSortingRepository<Lesson, Long> {

    @Query("select m from Meeting m left join m.lesson l where l.id = ?1")
    List<Meeting> getMeetings(long id);

    List<Lesson> findByTitle(String title);

    List<Lesson> findByHomeworkIgnoreCaseContaining(String subHomework);

    List<Lesson> findByCourse(Course course);
}
