package org.learnteachcode.seoul.attendance.tracker.api.attendance;

import org.learnteachcode.seoul.attendance.tracker.api.event.Event;
import org.learnteachcode.seoul.attendance.tracker.api.member.Member;

import javax.persistence.*;

@Entity(name = "attendance")
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;

    private String role;
}
