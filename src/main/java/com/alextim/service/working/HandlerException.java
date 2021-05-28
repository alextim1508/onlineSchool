package com.alextim.service.working;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class HandlerException {

    public static final String ERROR_STRING = "Операция с объектом %s не выполнена";
    public static final String DUPLICATE_ERROR_STRING = "Запись %s существует";
    public static final String EMPTY_RESULT_BY_ID_ERROR_STRING = "Объект %s c id %d не найден";
    public static final String ASSOCIATED_ERROR_STRING = "Объект не удалить/не обновить при ссылающихся на него других объектов";

    public static void handlerException(Exception exception, Object object) {
        String message = exception.getCause().getCause().getMessage();
        log.error("Exception: {} {}", exception.getClass(), message );

        if(message.contains("Duplicate entry"))
            throw new RuntimeException(String.format(DUPLICATE_ERROR_STRING, object));
        else if(message.contains("foreign key constraint fails") ||  message.contains("Нарушение ссылочной целостности"))
            throw new RuntimeException(String.format(ASSOCIATED_ERROR_STRING, object));
        else
            throw new RuntimeException(String.format(ERROR_STRING, object));
    }
}
