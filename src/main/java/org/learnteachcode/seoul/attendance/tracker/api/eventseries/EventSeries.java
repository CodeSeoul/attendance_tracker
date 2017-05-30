package org.learnteachcode.seoul.attendance.tracker.api.eventseries;

import org.learnteachcode.seoul.attendance.tracker.api.event.Event;

import javax.persistence.*;
import java.util.List;

@Entity(name = "event_series")
public class EventSeries {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @OneToMany(mappedBy = "eventSeries")
    private List<Event> eventList;
}
