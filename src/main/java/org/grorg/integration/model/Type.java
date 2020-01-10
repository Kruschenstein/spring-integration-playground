package org.grorg.integration.model;

import org.springframework.messaging.Message;

public enum Type {
    NUM,
    EXIT,
    ;

    public static Type from(Message<String> message) {
        if (message.getPayload().matches("\\d+")) {
            return Type.NUM;
        } else {
            return Type.EXIT;
        }
    }
}
