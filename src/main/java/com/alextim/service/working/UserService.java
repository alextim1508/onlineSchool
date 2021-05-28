package com.alextim.service.working;

import com.alextim.domain.Group;
import com.alextim.domain.Meeting;
import com.alextim.domain.User;
import com.alextim.security.GrantedAuthorityImpl;

import java.util.List;
import java.util.Set;

public interface UserService {

    User add(String username, String name, String surname, String email, String rawPassword);
    void setLock(long id, boolean lock);
    void setSms(long id, String sms);

    long getCount();
    List<User> getAll(int page, int amountByOnePage);

    User findById(long id);
    User findByUsername(String username);
    User findByEmail(String email);
    List<User> findByRole(GrantedAuthorityImpl.Role role);
    List<User> findByNameOrSurname(String name, String surname);

    Set<Group> getStudentsGroups(long id);
    Set<Group> getTeachersGroups(long id);
    List<Meeting> getMeeting(long id);

    void addRoles(long id, GrantedAuthorityImpl.Role... role);
    void removeRoles(long id, GrantedAuthorityImpl.Role... role);

    User update(User user, String username, String name, String surname, String email, String rawPassword);

    void delete(long id);
    void deleteAll();
}
