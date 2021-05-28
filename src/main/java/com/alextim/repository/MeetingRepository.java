package com.alextim.repository;

import com.alextim.domain.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MeetingRepository extends PagingAndSortingRepository<Meeting, Long> {

    List<Meeting> findByDateBetween(Date date1, Date date2);

    List<Meeting> findByLesson(Lesson lesson);

    List<Meeting> findByGroup(Group group);
}
