package org.thivernale.paymentservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JsonConverter {
    private final ObjectMapper mapper;

    public <T> T toObject(String message, Class<T> clazz) {
        try {
            return mapper.readValue(message, clazz);
        } catch (JsonProcessingException e) {
            log.error("JSON deserialization exception: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public String toString(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("JSON serialization exception: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
