package org.learnteachcode.seoul.attendance.tracker.api.attendee;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendeeRepository extends JpaRepository<Attendee, Long> {
    List<Attendee> findByUsernameIgnoreCaseContaining(String name);
}
