package org.learnteachcode.seoul.attendance.tracker.api.event;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
