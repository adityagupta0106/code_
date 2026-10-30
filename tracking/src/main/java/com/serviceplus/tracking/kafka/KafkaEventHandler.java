package com.serviceplus.tracking.kafka;

public interface KafkaEventHandler<T> {

    String getTopic();

    Class<T> getPayloadType();

    void process(T event);
}
