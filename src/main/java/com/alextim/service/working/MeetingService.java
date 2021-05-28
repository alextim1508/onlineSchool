package com.alextim.service.working;

import com.alextim.domain.*;

import java.util.Date;
import java.util.List;

public interface MeetingService {
    Meeting add(Date date, Lesson lesson, Group group);

    long getCount();
    List<Meeting> getAll(int page, int amountByOnePage);

    Meeting findById(long id);
    List<Meeting> findByBetweenDate(Date date1, Date date2);
    List<Meeting> findByLesson(Lesson lesson);
    List<Meeting> findByGroup(Group group);

    List<MeetingData> getMeetingData(long id);

    Meeting update(Meeting meeting, Date date, Lesson lesson, Group group);
    void delete(long id);
    void deleteAll();
}
