package com.alextim.repository;

import com.alextim.domain.User;
import com.alextim.security.GrantedAuthorityImpl;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository  extends PagingAndSortingRepository<User, Long> {

    @Query("select u from User u join u.authorities a where a.role = ?1")
    List<User> findByRole(@Param("role") GrantedAuthorityImpl.Role role);

    User findByUsername(String username);
    User findByEmail(String email);
    List<User> findByNameOrSurname(String name, String surname);
}
