package org.learnteachcode.seoul.attendance.tracker.api.attendee;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface AttendeeRepository extends PagingAndSortingRepository<Attendee, Long> {
    List<Attendee> findByUsernameIgnoreCaseContaining(String name);
}
