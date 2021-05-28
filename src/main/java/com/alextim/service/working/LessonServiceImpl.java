package com.alextim.service.working;

import com.alextim.domain.Course;
import com.alextim.domain.Lesson;
import com.alextim.domain.Meeting;
import com.alextim.repository.LessonRepository;
import com.alextim.service.security.SecurityService;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.alextim.service.working.HandlerException.EMPTY_RESULT_BY_ID_ERROR_STRING;
import static com.alextim.service.working.HandlerException.handlerException;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;

    private final SecurityService securityService;

    @PreAuthorize("hasPermission(#course, 'ADMINISTRATION') or hasRole('ROLE_MODERATOR') or hasRole('ROLE_ADMIN')")
    @Transactional
    @Override
    public Lesson add(String title, String homework, Course course) {
        Lesson lesson = new Lesson(title, course);
        lesson.setHomework(homework);
        try {
            lesson = lessonRepository.save(lesson);
            securityService.addSecurity(SecurityContextHolder.getContext().getAuthentication(),
                    lesson.getId(),
                    Lesson.class);
        }
        catch(DataIntegrityViolationException exception) {
            handlerException(exception, lesson);
        }
        return lesson;
    }

    @Override
    public long getCount() {
        return lessonRepository.count();
    }

    @Override
    public List<Lesson> getAll(int page, int amountByOnePage) {
        return lessonRepository.findAll(PageRequest.of(page,amountByOnePage)).getContent();
    }

    @Override
    public Lesson findById(long id) {
        return lessonRepository.findById(id).orElseThrow(()->
                new IllegalArgumentException(String.format(EMPTY_RESULT_BY_ID_ERROR_STRING, Lesson.class.getSimpleName(), id)));
    }

    @Override
    public List<Lesson> findByTitle(String title) {
        return lessonRepository.findByTitle(title);
    }

    @Override
    public List<Lesson> findByHomework(String subHomework) {
        return lessonRepository.findByHomeworkIgnoreCaseContaining(subHomework);
    }

    @Override
    public List<Lesson> findByCourse(Course course) {
        return lessonRepository.findByCourse(course);
    }

    @Transactional
    @Override
    public List<Meeting> getMeetings(long id) {
        Lesson lessonById = findById(id);
        List<Meeting> meetings = lessonById.getMeetings();
        int forEagerLoading = meetings.size();
        return meetings;
    }

    @PreAuthorize("hasPermission(#lesson, 'ADMINISTRATION') or hasRole('ROLE_MODERATOR') or hasRole('ROLE_ADMIN')")
    @Transactional
    @Override
    public Lesson update(Lesson lesson, String title, String homework, Course course) {
        if(title!= null)
            lesson.setTitle(title);
        if(homework!=null)
            lesson.setHomework(homework);
        if(course!=null)
            lesson.setCourse(course);

        try {
            lessonRepository.save(lesson);
        }
        catch(DataIntegrityViolationException exception) {
            handlerException(exception, lesson);
        }
        return lesson;
    }

    @Override
    public void delete(long id) {
        try {
            lessonRepository.deleteById(id);
        }
        catch (DataIntegrityViolationException exception) {
            handlerException(exception, null);
        }
    }

    @Override
    public void deleteAll() {
        try {
            lessonRepository.deleteAll();
        }
        catch (DataIntegrityViolationException exception) {
            handlerException(exception, Lesson.class);
        }
    }
}
