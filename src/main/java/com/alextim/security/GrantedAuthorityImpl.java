package com.alextim.security;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
@Data @AllArgsConstructor @NoArgsConstructor @EqualsAndHashCode
public class GrantedAuthorityImpl implements GrantedAuthority {

    public static final String COLUMN_ROLE = "role";

    @Enumerated(EnumType.STRING)
    @Column(name = COLUMN_ROLE)
    private Role role;

    @JsonIgnore
    @Override
    public String getAuthority(){
        return "ROLE_" + role.toString();
    }

    public enum Role {
        ADMIN,
        MODERATOR,
        TEACHER,
        STUDENT,
        GUEST
    }
}