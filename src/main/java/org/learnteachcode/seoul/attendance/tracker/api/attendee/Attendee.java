package org.learnteachcode.seoul.attendance.tracker.api.attendee;

import org.learnteachcode.seoul.attendance.tracker.api.attendance.Attendance;

import javax.persistence.*;
import java.util.List;

@Entity(name = "attendee")
public class Attendee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String username;

    @OneToMany(mappedBy = "attendee")
    private List<Attendance> attendanceList;
}
