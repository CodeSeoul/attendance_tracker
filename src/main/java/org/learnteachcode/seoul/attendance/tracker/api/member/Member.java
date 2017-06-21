package org.learnteachcode.seoul.attendance.tracker.api.member;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import org.learnteachcode.seoul.attendance.tracker.api.attendance.Attendance;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "member")
@Data
@ToString(exclude = "password")
public class Member {
    public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username, givenName, familyName;

    @JsonIgnore
    private String password;

    private String[] roles;

    @OneToMany(mappedBy = "member")
    private List<Attendance> attendanceList;

    public Member() {
        attendanceList = new ArrayList<>();
    }

    public Member(String username, String password, String... roles) {
        this.username = username;
        this.password = PASSWORD_ENCODER.encode(password);
        this.roles = roles;
        this.attendanceList = new ArrayList<>();
    }

    public void setPassword(String password) {
        this.password = PASSWORD_ENCODER.encode(password);
    }
}
