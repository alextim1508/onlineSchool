package com.alextim.domain;

import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

import static com.alextim.domain.Group.COLUMN_TITLE;
import static com.alextim.domain.Group.TABLE;


@Entity @Table(name = TABLE, uniqueConstraints= @UniqueConstraint(columnNames={COLUMN_TITLE}))
@Data @NoArgsConstructor @RequiredArgsConstructor
@EqualsAndHashCode(exclude = {"id", "students", "teachers", "meetings"}) @ToString(exclude = {"students",  "teachers", "meetings"})
public class Group {

    public static final String TABLE = "Parties";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_COURSE_ID = "course_id";
    public static final String COLUMN_STATUS = "status";

    public static final String TABLE_COLLECTION_TEACHER = "Parties_Teachers";
    public static final String COLUMN_JOIN_COLLECTION_GROUP = "party_id";
    public static final String TABLE_COLLECTION_STUDENT = "Parties_Students";
    public static final String COLUMN_JOIN_COLLECTION_USER = "user_id";


    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private long id;

    @Column(name = COLUMN_TITLE, nullable = false)
    @NonNull
    private String title;

    @ManyToOne @JoinColumn(name = COLUMN_COURSE_ID, nullable = false)
    @NonNull
    private Course course;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = TABLE_COLLECTION_STUDENT,
            joinColumns = @JoinColumn(name = COLUMN_JOIN_COLLECTION_GROUP),
            inverseJoinColumns = @JoinColumn(name = COLUMN_JOIN_COLLECTION_USER))
    private Set<User> students;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = TABLE_COLLECTION_TEACHER,
            joinColumns = @JoinColumn(name = COLUMN_JOIN_COLLECTION_GROUP),
            inverseJoinColumns = @JoinColumn(name = COLUMN_JOIN_COLLECTION_USER))
    private Set<User> teachers;

    @OneToMany( mappedBy = "group", fetch = FetchType.LAZY)
    @Setter(AccessLevel.NONE)
    private List<Meeting> meetings;

    @Enumerated(EnumType.STRING) @Column(name = COLUMN_STATUS, nullable = false)
    private Status status = Status.START;

    public enum Status {
        START,
        RUN,
        DONE
    }
}
