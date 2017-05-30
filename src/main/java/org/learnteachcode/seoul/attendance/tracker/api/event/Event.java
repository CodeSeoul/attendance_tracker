package org.learnteachcode.seoul.attendance.tracker.api.event;

import org.learnteachcode.seoul.attendance.tracker.api.attendance.Attendance;
import org.learnteachcode.seoul.attendance.tracker.api.eventseries.EventSeries;

import javax.persistence.*;
import java.util.List;

@Entity(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @OneToMany(mappedBy = "event")
    private List<Attendance> attendanceList;

    @ManyToOne
    @JoinColumn(name = "event_series_id", referencedColumnName = "id")
    private EventSeries eventSeries;
}
