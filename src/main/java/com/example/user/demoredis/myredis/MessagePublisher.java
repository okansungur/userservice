package com.example.user.demoredis.myredis;

public interface MessagePublisher {

    void publish(final String message);
}