package org.learnteachcode.seoul.attendance.tracker.api.attendance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByEventId(long eventId);

    List<Attendance> findByAttendeeId(long attendeeId);
}
