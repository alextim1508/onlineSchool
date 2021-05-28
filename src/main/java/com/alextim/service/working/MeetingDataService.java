package com.alextim.service.working;

import com.alextim.domain.Meeting;
import com.alextim.domain.MeetingData;

import java.util.List;

public interface MeetingDataService {

    MeetingData add(String title, String url, Meeting meeting);

    long getCount();
    List<MeetingData> getAll(int page, int amountByOnePage);

    MeetingData findById(long id);
    List<MeetingData> find(Meeting meeting);

    MeetingData update(MeetingData meetingData, String title, String url, Meeting meeting);
    void delete(long id);
    void deleteAll();
}
