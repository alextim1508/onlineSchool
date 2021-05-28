package com.alextim.domain;

import lombok.*;

import javax.persistence.*;
import java.util.List;

import static com.alextim.domain.Course.COLUMN_TITLE;
import static com.alextim.domain.Course.TABLE;


@Entity @Table(name = TABLE, uniqueConstraints= @UniqueConstraint(columnNames={COLUMN_TITLE}))
@Data @NoArgsConstructor @RequiredArgsConstructor
@EqualsAndHashCode(exclude = {"id", "lessons", "groups"}) @ToString(exclude = {"lessons", "groups"})
public class Course {

    public static final String TABLE = "Courses";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private long id;

    @Column(name = COLUMN_TITLE, nullable = false)
    @NonNull
    private String title;

    @Column(name = COLUMN_DESCRIPTION, nullable = false)
    @NonNull
    private String description;

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    @Setter(AccessLevel.NONE)
    private List<Lesson> lessons;

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    @Setter(AccessLevel.NONE)
    private List<Group> groups;
}

