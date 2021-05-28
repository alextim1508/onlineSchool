package com.alextim.domain;

import lombok.*;

import javax.persistence.*;
import java.util.List;

import static com.alextim.domain.Lesson.*;

@Entity @Table(name = TABLE, uniqueConstraints= @UniqueConstraint(columnNames={COLUMN_TITLE, COLUMN_COURSE_ID}))
@Data @NoArgsConstructor @RequiredArgsConstructor
@EqualsAndHashCode(exclude = {"id", "meetings"}) @ToString(exclude = {"meetings"})
public class Lesson {

    public static final String TABLE = "Lessons";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_HOMEWORK = "homework";
    public static final String COLUMN_COURSE_ID = "course_id";

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private long id;

    @Column(name = COLUMN_TITLE, nullable = false)
    @NonNull
    private String title;

    @Column(name = COLUMN_HOMEWORK)
    private String homework;

    @ManyToOne @JoinColumn(name = COLUMN_COURSE_ID, nullable = false)
    @NonNull
    private Course course;

    @OneToMany(mappedBy = "lesson", fetch = FetchType.LAZY)
    @Setter(AccessLevel.NONE)
    private List<Meeting> meetings;
}
