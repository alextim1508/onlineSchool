package com.alextim.domain;

import lombok.*;

import javax.persistence.*;

import static com.alextim.domain.MeetingData.TABLE;

@Entity @Table(name = TABLE)
@Data @NoArgsConstructor @RequiredArgsConstructor @EqualsAndHashCode(exclude = {"id"}) @ToString
public class MeetingData {

    public static final String TABLE = "Meeting_Data";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_URL = "url";
    public static final String COLUMN_MEETING_ID = "meeting_id";

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private long id;

    @Column(name = COLUMN_TITLE, nullable = false)
    @NonNull
    private String title;

    @Column(name = COLUMN_URL, nullable = false)
    @NonNull
    private String url;

    @ManyToOne
    @JoinColumn(name = COLUMN_MEETING_ID, nullable = false)
    @NonNull
    private Meeting meeting;
}
