package org.learnteachcode.seoul.attendance.tracker.api.eventseries;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.learnteachcode.seoul.attendance.tracker.api.event.Event;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "event_series")
public class EventSeries {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "eventSeries")
    private List<Event> eventList;

    public EventSeries(String name) {
        this.name = name;
        this.eventList = new ArrayList<>();
    }
}
