package com.alextim.domain;

import com.alextim.security.GrantedAuthorityImpl;
import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;

import java.util.*;

import static com.alextim.domain.User.COLUMN_EMAIL;
import static com.alextim.domain.User.COLUMN_USER_NAME;
import static com.alextim.domain.User.TABLE;

@Entity @Table(name = TABLE, uniqueConstraints= {@UniqueConstraint(columnNames={COLUMN_USER_NAME}), @UniqueConstraint(columnNames={COLUMN_EMAIL})})
@Data @NoArgsConstructor @RequiredArgsConstructor
@AllArgsConstructor @Builder @EqualsAndHashCode(exclude = {"id", "studentsGroups", "teachersGroups", "meetingsUsers"})
@ToString(exclude = {"password", "studentsGroups", "teachersGroups", "meetingsUsers"})
public class User implements UserDetails {

    public static final String TABLE = "Users";
    public static final String COLUMN_USER_NAME = "Username";
    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_SURNAME = "Surname";
    public static final String COLUMN_EMAIL = "Email";
    public static final String COLUMN_PHONE = "Phone";
    public static final String COLUMN_SMS = "SentSms";
    public static final String COLUMN_PASSWORD = "EncodedPassword";

    public static final String COLUMN_AUTHORITIES = "Authorities";
    public static final String COLUMN_ACCOUNT_NON_EXPIRED = "AccountNonExpired";
    public static final String COLUMN_ACCOUNT_NON_LOCKED = "AccountNonLocked";
    public static final String COLUMN_CREDENTIALS_NON_LOCKED = "CredentialsNonExpired";
    public static final String COLUMN_ENABLED = "Enabled";

    public static final String TABLE_COLLECTION = "Roles";
    public static final String COLUMN_JOIN_COLLECTION = "user_id";

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private long id;

    @NonNull
    @Column(name = COLUMN_USER_NAME, nullable = false)
    private String username;

    @NonNull
    @Column(name = COLUMN_NAME, nullable = false)
    private String name;

    @NonNull
    @Column(name = COLUMN_SURNAME, nullable = false)
    private String surname;

    @NonNull
    @Column(name = COLUMN_EMAIL, nullable = false)
    private String email;

    @NonNull
    @Column(name = COLUMN_PASSWORD, nullable = false)
    private String password;

    @Column(name = COLUMN_PHONE)
    private String phone;

    @Column(name = COLUMN_SMS)
    private String sms;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = TABLE_COLLECTION, joinColumns = @JoinColumn(name = COLUMN_JOIN_COLLECTION))
    @Singular
    private Set<GrantedAuthorityImpl> authorities;

    @Column(name = COLUMN_ACCOUNT_NON_EXPIRED)
    private boolean accountNonExpired;

    @Column(name = COLUMN_ACCOUNT_NON_LOCKED)
    private boolean accountNonLocked;

    @Column(name = COLUMN_CREDENTIALS_NON_LOCKED)
    private boolean credentialsNonExpired;

    @Column(name = COLUMN_ENABLED)
    private boolean enabled;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "students")
    @Setter(AccessLevel.NONE)
    private Set<Group> studentsGroups;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "teachers")
    @Setter(AccessLevel.NONE)
    private Set<Group> teachersGroups;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "meetingUser.user")
    @Setter(AccessLevel.NONE)
    private List<MeetingsUsers> meetingsUsers;
}
