package com.leon.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.leon.common.exception.BizException;

import java.util.*;

public final class JsonUtils {

    private JsonUtils() {
        throw new IllegalStateException("non-instantiable utility class");
    }

    public static ObjectMapper getMapper() {
        return JsonUtils.ObjectMapperHolder.createConfiguredMapper();
    }

    public static String toString(Object value) {
        if (value == null) {
            return null;
        } else {
            try {
                return getMapper().writeValueAsString(value);
            } catch (JsonProcessingException var2) {
                throw new BizException("50", "json exception");
            }
        }
    }



    public static <T> T parseObject(String jsonStr, Class<T> type) {
        if (jsonStr == null) {
            return null;
        } else {
            try {
                return getMapper().readValue(jsonStr, type);
            } catch (JsonProcessingException var3) {
                throw new BizException("50", "json exception");
            }
        }
    }

    public static <T> T parseObject(String jsonStr, TypeReference<T> typeReference) {
        if (jsonStr == null) {
            return null;
        } else {
            try {
                return getMapper().readValue(jsonStr, typeReference);
            } catch (JsonProcessingException var3) {
                throw new BizException("50", "json exception");
            }
        }
    }


    private static class ObjectMapperHolder {

        public static ObjectMapper createConfiguredMapper() {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            mapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
            mapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, true);
            mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            mapper.setTimeZone(TimeZone.getTimeZone("GMT"));
            mapper.registerModule(new JavaTimeModule());
            return mapper;
        }
    }
}
