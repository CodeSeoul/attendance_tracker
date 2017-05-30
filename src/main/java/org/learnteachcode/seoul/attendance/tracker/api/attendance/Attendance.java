package org.learnteachcode.seoul.attendance.tracker.api.attendance;

import org.learnteachcode.seoul.attendance.tracker.api.attendee.Attendee;
import org.learnteachcode.seoul.attendance.tracker.api.event.Event;

import javax.persistence.*;

@Entity(name = "attendance")
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "attendee_id", referencedColumnName = "id")
    private Attendee attendee;
}
