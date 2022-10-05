package org.goldgame.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WrongParameterException extends RuntimeException {
    public WrongParameterException(String s) {
        super(s);
        log.info("404 {}", getMessage());
    }
}