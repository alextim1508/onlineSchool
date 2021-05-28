package com.alextim.service.working;

import com.alextim.domain.Group;
import com.alextim.domain.Meeting;
import com.alextim.domain.MeetingsUsers;
import com.alextim.domain.User;
import com.alextim.repository.UserRepository;
import com.alextim.security.GrantedAuthorityImpl;
import com.alextim.security.GrantedAuthorityImpl.Role;
import com.alextim.service.security.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.alextim.service.working.HandlerException.EMPTY_RESULT_BY_ID_ERROR_STRING;
import static com.alextim.service.working.HandlerException.handlerException;

@Service
@RequiredArgsConstructor @Slf4j
public class UserDetailsServiceImpl implements UserDetailsService, UserService {

    private final UserRepository userRepository;

    private final SecurityService securityService;

    @Override
    public User add(String username, String name, String surname, String email, String rawPassword) {
        User user = User.builder()
                .username(username)
                .name(name)
                .surname(surname)
                .email(email)
                .password(encode(rawPassword))
                .authority(new GrantedAuthorityImpl(Role.GUEST))
                .accountNonExpired(true)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();

        try {
            userRepository.save(user);
            securityService.addSecurity(user.getUsername(), user.getId(), User.class);
        }
        catch (DataIntegrityViolationException exception) {
            handlerException(exception, user);
        }
        return user;
    }

    @Transactional
    @Override
    public void addRoles(long id, Role... role) {
        User user = findById(id);
        Collection<GrantedAuthorityImpl> authorities = user.getAuthorities();
        authorities.addAll(Arrays.stream(role).map(GrantedAuthorityImpl::new).collect(Collectors.toList()));
    }

    @Transactional
    @Override
    public void removeRoles(long id, Role... role) {
        User user = findById(id);
        Collection<GrantedAuthorityImpl> authorities = user.getAuthorities();
        authorities.removeAll(Arrays.stream(role).map(GrantedAuthorityImpl::new).collect(Collectors.toList()));
    }

    @Transactional
    @Override
    public void setLock(long id, boolean lock) {
        findById(id).setAccountNonLocked(!lock);
    }

    @Transactional
    @Override
    public void setSms(long id, String sms) {
        findById(id).setSms(sms);
    }

    @Override
    public long getCount() {
        return userRepository.count();
    }

    @Override
    public List<User> getAll(int page, int amountByOnePage) {
        return userRepository.findAll(PageRequest.of(page,amountByOnePage)).getContent();
    }

    @PostAuthorize("hasPermission(returnObject, 'ADMINISTRATION') or hasRole('ROLE_MODERATOR') or hasRole('ROLE_ADMIN')")
    @Override
    public User findById(long id) {
        return userRepository.findById(id).orElseThrow(()->
                new IllegalArgumentException(String.format(EMPTY_RESULT_BY_ID_ERROR_STRING, User.class.getSimpleName(), id)));
    }

    @Override
    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> findByNameOrSurname(String name, String surname) {
        return userRepository.findByNameOrSurname(name, surname);
    }

    @PostFilter("hasPermission(filterObject, 'READ') or hasRole('ROLE_MODERATOR') or hasRole('ROLE_ADMIN')")
    @Transactional
    @Override
    public Set<Group> getStudentsGroups(long id) {
        User userById = findById(id);
        Set<Group> studentsGroups = userById.getStudentsGroups();
        int forEagerLoading = studentsGroups.size();
        return studentsGroups;
    }

    @PostFilter("hasPermission(filterObject, 'READ') or hasRole('ROLE_MODERATOR') or hasRole('ROLE_ADMIN')")
    @Override
    public Set<Group> getTeachersGroups(long id) {
        User userById = findById(id);
        Set<Group> teachersGroups = userById.getTeachersGroups();
        int forEagerLoading = teachersGroups.size();

        return teachersGroups;
    }

    @PostFilter("hasPermission(filterObject, 'READ') or hasRole('ROLE_MODERATOR') or hasRole('ROLE_ADMIN')")
    @Transactional
    @Override
    public List<Meeting> getMeeting(long id) {
        User userById = findById(id);
        List<MeetingsUsers> meetingsUsers = userById.getMeetingsUsers();
        int forEagerLoading = meetingsUsers.size();

        List<Meeting> meetings = new ArrayList<>();
        meetingsUsers.forEach(meetingsUsers1 -> meetings.add(meetingsUsers1.getMeetingUser().getMeeting()));

        return meetings;
    }

    @PreAuthorize("hasPermission(#user, 'ADMINISTRATION') or hasRole('ROLE_MODERATOR') or hasRole('ROLE_ADMIN')")
    @Transactional
    @Override
    public User update(User user, String username, String name, String surname, String email, String rawPassword) {
        if(username != null)
            user.setUsername(username);
        if(name != null)
            user.setName(name);
        if(surname != null)
            user.setSurname(surname);
        if(email != null)
            user.setEmail(email);
        if(rawPassword != null)
            user.setEmail(rawPassword);

        try {
            userRepository.save(user);
        }
        catch (DataIntegrityViolationException exception) {
            handlerException(exception, user);
        }
        return user;
    }

    @Override
    public void delete(long id) {
        try {
            userRepository.deleteById(id);
        }
        catch (DataIntegrityViolationException exception) {
            handlerException(exception, null);
        }
    }

    @Override
    public void deleteAll() {
        try {
            userRepository.deleteAll();
        }
        catch (DataIntegrityViolationException exception) {
            handlerException(exception, null);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if(user == null) {
            log.info("{} not found", username);
            throw new UsernameNotFoundException(username + " not found");
        }
        log.info("Loaded user from storage: " + user);
        return user;
    }

    public static String encode(String password) {
        return password;
    }
}
