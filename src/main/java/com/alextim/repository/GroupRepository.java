package com.alextim.repository;

import com.alextim.domain.Course;
import com.alextim.domain.Group;
import com.alextim.domain.Lesson;
import com.alextim.domain.Meeting;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends PagingAndSortingRepository<Group, Long> {

    List<Group> findByTitle(String title);

    List<Group> findByCourse(Course course);

    @Query("select m from Meeting m left join m.group g where g.id = ?1")
    List<Meeting> getMeetings(long id);
}
