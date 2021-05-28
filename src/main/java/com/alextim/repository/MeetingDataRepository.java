package com.alextim.repository;

import com.alextim.domain.Meeting;
import com.alextim.domain.MeetingData;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingDataRepository extends PagingAndSortingRepository<MeetingData, Long> {

    List<MeetingData> findByMeeting(Meeting meeting);
}
