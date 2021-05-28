package com.alextim.domain;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

import static com.alextim.domain.Meeting.*;

@Entity @Table(name = TABLE, uniqueConstraints= @UniqueConstraint(columnNames={COLUMN_DATE, COLUMN_LESSON_ID, COLUMN_GROUP_ID}))
@Data @NoArgsConstructor @RequiredArgsConstructor @EqualsAndHashCode(exclude = {"id", "meetingsUsers", "meetingData"}) @ToString(exclude = {"meetingsUsers",  "meetingData"})
public class Meeting {

    public static final String TABLE = "Meetings";
    public static final String COLUMN_DATE = "meetingDate";
    public static final String COLUMN_LESSON_ID = "lesson_id";
    public static final String COLUMN_GROUP_ID = "party_id";

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private long id;

    @Column(name = COLUMN_DATE, nullable = false)
    @NonNull
    private Date date;

    @ManyToOne @JoinColumn(name = COLUMN_LESSON_ID, nullable = false)
    @NonNull
    private Lesson lesson;

    @ManyToOne @JoinColumn(name = COLUMN_GROUP_ID, nullable = false)
    @NonNull
    private Group group;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "meetingUser.meeting", cascade = CascadeType.ALL)
    private List<MeetingsUsers> meetingsUsers;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "meeting")
    @Setter(AccessLevel.NONE)
    private List<MeetingData> meetingData;
}
