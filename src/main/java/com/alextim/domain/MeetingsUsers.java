package com.alextim.domain;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

import static com.alextim.domain.MeetingsUsers.TABLE;

@Entity @Table(name = TABLE)
@Data @NoArgsConstructor @RequiredArgsConstructor @AllArgsConstructor
public class MeetingsUsers {

    public static final String TABLE = "Meetings_Users";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_MEETING_ID = "meeting_id";
    public static final String COLUMN_PRESENCE = "presence";

    @EmbeddedId
    private MeetingUser meetingUser;

    @Column(name = COLUMN_PRESENCE)
    @NonNull
    boolean presence;

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class MeetingUser implements Serializable {
        @ManyToOne @JoinColumn(name = COLUMN_USER_ID)
        @NonNull
        private User user;

        @ManyToOne @JoinColumn(name = COLUMN_MEETING_ID)
        @NonNull
        private Meeting meeting;
    }
}
