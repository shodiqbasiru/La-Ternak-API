package com.enigma.laternak.util;

import com.enigma.laternak.constant.Message;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ResponseMessage {
    public static String getMessage(Message message) {
        return message.getMessage();
    }

    public static ResponseStatusException error(HttpStatus status, Message message) {
        return new ResponseStatusException(status, getMessage(message));
    }

    public static ResponseStatusException error(HttpStatus status, String message) {
        return new ResponseStatusException(status, message);
    }
}
