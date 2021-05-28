package com.alextim.service.security;

import com.alextim.domain.User;
import com.alextim.security.GrantedAuthorityImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor @Slf4j
public class SecurityServiceImpl implements SecurityService {

    private final MutableAclService aclService;

    @Override
    public void addSecurity(Authentication authentication, long id, Class<?> type) {
        addSecurity(new PrincipalSid(authentication), id, type);
    }

    @Override
    public void addSecurity(String principal, long id, Class<?> type) {
        addSecurity(new PrincipalSid(principal), id, type);
    }

    private void addSecurity(Sid sidOwner, long id, Class<?> type) {
        AuditableAcl acl = (AuditableAcl)aclService.createAcl(new ObjectIdentityImpl(type.getName(), id));
        acl.setOwner(sidOwner);
        acl.setParent(null);
        acl.setEntriesInheriting(false);

        acl.insertAce(acl.getEntries().size(), BasePermission.ADMINISTRATION, sidOwner, true);
        acl.updateAuditing(acl.getEntries().size()-1, true, true);

        acl.insertAce(acl.getEntries().size(), BasePermission.ADMINISTRATION, new GrantedAuthoritySid(new GrantedAuthorityImpl(GrantedAuthorityImpl.Role.ADMIN)), true);
        acl.updateAuditing(acl.getEntries().size()-1, true, true);

        aclService.updateAcl(acl);
        log.info("{} add permission ADMINISTRATION to {} with id {} ", sidOwner, type.getSimpleName(), id);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Override
    public void addPermission(long id, Class<?> type, String principal, Permission permission) {
        MutableAcl acl = (MutableAcl)aclService.readAclById(new ObjectIdentityImpl(type.getName(), id));
        acl.insertAce(acl.getEntries().size(), permission, new PrincipalSid(principal), true);
        log.info("{} add permission {} to {} with id {} ", principal, permission, type.getSimpleName(), id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Override
    public void subPermission(long id, Class<?> type, String principal, Permission permission) {
        //Todo !!
    }




    @Override
    public boolean isGranted(long id, Class<?> type, String principal, Permission... permission) {
        return isGranted(id, type, principal, null, permission);
    }

    @Override
    public boolean isGranted(long id, Class<?> type, Authentication authentication, Permission... permission) {
        return isGranted(id, type, null, authentication, permission);
    }

    private boolean isGranted(long id, Class<?> type, String principal, Authentication authentication, Permission... permission) {
        Acl acl = aclService.readAclById(new ObjectIdentityImpl(type.getName(), id));
        List<Permission> permissions = Arrays.asList(permission);

        List<Sid> sids = new ArrayList<>();
        if(principal != null) {
            sids.add(new PrincipalSid(principal));
        }
        if(authentication != null) {
            sids.add(new PrincipalSid(authentication));
            sids.addAll(((User)authentication.getPrincipal()).getAuthorities().stream().map(GrantedAuthoritySid::new).collect(Collectors.toList()));
        }

        try {
            boolean isGranted = acl.isGranted(permissions, sids, true);

            AtomicReference<String> splitter = new AtomicReference<>("");
            StringBuilder sidsBuilder = new StringBuilder();
            sids.forEach(sid -> { sidsBuilder.append(splitter).append(sid); splitter.set(", "); });
            StringBuilder permissionsBuilder = new StringBuilder();
            permissions.forEach(per -> { permissionsBuilder.append(splitter).append(per); splitter.set(", "); });

            log.info("{} {} {} for {} with id {} ",  isGranted ? "is granted" : "is not granted", sidsBuilder, permissionsBuilder, type.getSimpleName(), id);
            return isGranted;
        }
        catch(NotFoundException e) {
            log.error("NotFoundException: {}", e.getMessage() );
            return false;
        }
    }
}