package com.alextim.security.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.alextim.security.GrantedAuthorityImpl.Role.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration  extends WebSecurityConfigurerAdapter {

    private final AuthenticationProcessingFilter authenticationProcessingFilter;

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/h2-console/**");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .and()
                .authorizeRequests().antMatchers( "/", "/public").permitAll()
                .and()
                .authorizeRequests().antMatchers("/authenticated").authenticated()

                .antMatchers(HttpMethod.GET, "/course").permitAll()
                .antMatchers(HttpMethod.GET, "/course/find").permitAll()
                .antMatchers(HttpMethod.POST, "/course").hasAnyRole(TEACHER.toString(), MODERATOR.toString(), ADMIN.toString())
                .antMatchers(HttpMethod.PUT, "/course/{\\d+}").hasAnyRole(TEACHER.toString(), MODERATOR.toString(), ADMIN.toString())
                .antMatchers(HttpMethod.DELETE, "/course/**").hasAnyRole(MODERATOR.toString(), ADMIN.toString())

                .antMatchers(HttpMethod.GET, "/lesson").permitAll()
                .antMatchers(HttpMethod.GET, "/lesson/find").permitAll()
                .antMatchers(HttpMethod.POST, "/lesson").hasAnyRole(TEACHER.toString(), MODERATOR.toString(), ADMIN.toString())
                .antMatchers(HttpMethod.PUT, "/lesson/[\\d+]").hasAnyRole(TEACHER.toString(), MODERATOR.toString(), ADMIN.toString())
                .antMatchers(HttpMethod.DELETE, "/lesson/**").hasAnyRole(MODERATOR.toString(), ADMIN.toString())

                .antMatchers("/group/**").hasAnyRole(MODERATOR.toString(), ADMIN.toString())

                .antMatchers(HttpMethod.PUT, "/user/[\\d+]").hasAnyRole(GUEST.toString(), MODERATOR.toString(), ADMIN.toString())
                .antMatchers(HttpMethod.GET, "/user/[\\d+]").hasAnyRole(GUEST.toString(), MODERATOR.toString(), ADMIN.toString())
                .and().authorizeRequests().antMatchers(HttpMethod.GET, "/user/groups/student/{\\d+}").hasAnyRole("STUDENT")
                .and().authorizeRequests().antMatchers(HttpMethod.GET, "/user").hasAnyRole("STUDENT")
                .antMatchers(HttpMethod.GET, "/user/groups/teacher/[\\d+]").hasAnyRole(TEACHER.toString(), MODERATOR.toString(), ADMIN.toString())
                .antMatchers(HttpMethod.GET, "/user/groups/meeting/[\\d+]").hasAnyRole(TEACHER.toString(), STUDENT.toString(), MODERATOR.toString(), ADMIN.toString())
                .antMatchers(HttpMethod.POST,  "/user/present").hasAnyRole(TEACHER.toString(), MODERATOR.toString(), ADMIN.toString())
                .antMatchers(HttpMethod.GET,  "/user/present/[\\d+]").hasAnyRole(STUDENT.toString(), MODERATOR.toString(), ADMIN.toString())
                .antMatchers("/user/**").hasAnyRole(ADMIN.toString())

                .antMatchers(HttpMethod.POST, "/meeting").hasAnyRole(TEACHER.toString(), MODERATOR.toString(), ADMIN.toString())
                .antMatchers(HttpMethod.GET, "/meeting").hasAnyRole(TEACHER.toString(), STUDENT.toString(), MODERATOR.toString(), ADMIN.toString())
                .antMatchers(HttpMethod.PUT, "/meeting/[\\d+]").hasAnyRole(TEACHER.toString(), MODERATOR.toString(), ADMIN.toString())
                .antMatchers("/meeting/**").hasAnyRole(MODERATOR.toString(), ADMIN.toString())

                .antMatchers(HttpMethod.POST, "/meetingData").hasAnyRole(TEACHER.toString(), MODERATOR.toString(), ADMIN.toString())
                .antMatchers(HttpMethod.GET, "/meetingData").hasAnyRole(STUDENT.toString(), TEACHER.toString(), MODERATOR.toString(), ADMIN.toString())
                .antMatchers(HttpMethod.PUT, "/meetingData/[\\d+]").hasAnyRole(TEACHER.toString(), MODERATOR.toString(), ADMIN.toString())

                .and()
                .addFilterBefore(authenticationProcessingFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin().loginPage("/login")
                .and()
                .logout().logoutSuccessUrl("/").logoutUrl("/logout")
        ;
    }
}