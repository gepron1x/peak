package org.gepron1x.peak.service;

/**
 * Ошибка бизнес-правила. Сообщение показывается пользователю в интерфейсе.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
