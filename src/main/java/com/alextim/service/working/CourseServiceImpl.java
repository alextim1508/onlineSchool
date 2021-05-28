package com.alextim.service.working;

import com.alextim.domain.Course;
import com.alextim.domain.Group;
import com.alextim.domain.Lesson;
import com.alextim.repository.CourseRepository;
import com.alextim.service.gateway.SecretService;
import com.alextim.service.security.SecurityService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SigningKeyResolverAdapter;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Hibernate;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.alextim.service.working.HandlerException.EMPTY_RESULT_BY_ID_ERROR_STRING;
import static com.alextim.service.working.HandlerException.handlerException;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    private final SecurityService securityService;

    @Override
    public Course add(String title, String description) {
        Course course = new Course(title, description);
        try {
            course = courseRepository.save(course);
            securityService.addSecurity(SecurityContextHolder.getContext().getAuthentication(),
                    course.getId(),
                    Course.class);
       }
        catch (DataIntegrityViolationException exception) {
            handlerException(exception, course);
        }
        return course;
    }

    @Override
    public long getCount() {
        return courseRepository.count();
    }

    @Override
    public List<Course> getAll(int page, int amountByOnePage) {
        return courseRepository.findAll(PageRequest.of(page,amountByOnePage)).getContent();
    }

    @Override
    public Course findById(long id) {
        return courseRepository.findById(id).orElseThrow(()->
                new IllegalArgumentException(String.format(EMPTY_RESULT_BY_ID_ERROR_STRING, Course.class.getSimpleName(), id)));
    }

    @Override
    public List<Course> findByTitle(String title) {
        return courseRepository.findByTitle(title);
    }

    @Override
    public List<Course> findBySubDescription(String subDescription) {
        return courseRepository.findByDescriptionIgnoreCaseContaining(subDescription);
    }

    @Transactional
    @Override
    public List<Lesson> getLesson(long id) {
        Course courseById = findById(id);
        List<Lesson> lessons = courseById.getLessons();
        int forEagerLoading = lessons.size();
        return lessons;
    }

    @Transactional
    @Override
    public List<Group> getGroup(long id) {
        Course groupById = findById(id);
        List<Group> groups = groupById.getGroups();
        int forEagerLoading = groups.size();
        return groups;
    }

    @PreAuthorize("hasPermission(#course, 'ADMINISTRATION') or hasRole('ROLE_MODERATOR') or hasRole('ROLE_ADMIN')")
    @Transactional
    @Override
    public Course update(Course course, String title, String description) {
        if(title!= null)
            course.setTitle(title);
        if(description!= null)
            course.setDescription(description);

        try {
            courseRepository.save(course);
        }
        catch (DataIntegrityViolationException exception) {
            handlerException(exception, course);
        }
        return course;
    }

    @Override
    public void delete(long id) {
        try {
            courseRepository.deleteById(id);
        }
        catch (DataIntegrityViolationException exception) {
            handlerException(exception, null);
        }
    }

    @Override
    public void deleteAll() {
        try {
            courseRepository.deleteAll();
        }
        catch (DataIntegrityViolationException exception) {
            handlerException(exception, Course.class);
        }
    }
}
