package org.learnteachcode.seoul.attendance.tracker.api.attendance;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface AttendanceRepository extends PagingAndSortingRepository<Attendance, Long> {
    List<Attendance> findByEventId(long eventId);

    List<Attendance> findByMemberId(long memberId);
}
