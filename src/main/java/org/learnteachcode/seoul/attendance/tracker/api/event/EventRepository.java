package org.learnteachcode.seoul.attendance.tracker.api.event;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface EventRepository extends PagingAndSortingRepository<Event, Long> {
}
