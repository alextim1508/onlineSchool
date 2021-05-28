package com.alextim.service.security;

import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;

public interface SecurityService {

    void addSecurity(Authentication authentication, long id, Class<?> type);
    void addSecurity(String principal, long id, Class<?> type);

    void addPermission(long id, Class<?> type, String principal, Permission permission);
    void subPermission(long id, Class<?> type, String principal, Permission permission);

    boolean isGranted(long id, Class<?> type, Authentication authentication, Permission... permission);
    boolean isGranted(long id, Class<?> type, String principal, Permission... permission);
}