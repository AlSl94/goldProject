package org.goldgame.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidationException extends RuntimeException {
    public ValidationException(String s) {
        super(s);
        log.info("400 {}", getMessage());
    }
}