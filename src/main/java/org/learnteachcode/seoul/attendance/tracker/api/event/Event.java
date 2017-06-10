package org.learnteachcode.seoul.attendance.tracker.api.event;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.learnteachcode.seoul.attendance.tracker.api.attendance.Attendance;
import org.learnteachcode.seoul.attendance.tracker.api.eventseries.EventSeries;
import org.learnteachcode.seoul.attendance.tracker.api.organizer.Organizer;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private ZonedDateTime eventStart;

    private ZonedDateTime eventEnd;

    @OneToMany(mappedBy = "event")
    private List<Attendance> attendanceList;

    @ManyToOne
    @JoinColumn(name = "event_series_id", referencedColumnName = "id")
    private EventSeries eventSeries;

    @ManyToOne
    @JoinColumn(name = "organizer_id", referencedColumnName = "id")
    private Organizer organizer;

    public Event(String name, ZonedDateTime startTime, ZonedDateTime endTime, EventSeries eventSeries, Organizer organizer) {
        this.name = name;
        this.eventStart = startTime;
        this.eventEnd = endTime;
        this.eventSeries = eventSeries;
        this.attendanceList = new ArrayList<>();
    }
}
