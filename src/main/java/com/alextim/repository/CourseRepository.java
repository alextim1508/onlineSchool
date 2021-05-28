package com.alextim.repository;

import com.alextim.domain.Course;
import com.alextim.domain.Group;
import com.alextim.domain.Lesson;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends PagingAndSortingRepository<Course, Long> {

    @Query("select l from Lesson l left join l.course c where c.id = ?1")
    List<Lesson> getLessons(long id);

    @Query("select g from Group g left join g.course c where c.id = ?1")
    List<Group> getGroups(long id);

    List<Course> findByTitle(String title);

    List<Course> findByDescriptionIgnoreCaseContaining(String subDescription);
}
