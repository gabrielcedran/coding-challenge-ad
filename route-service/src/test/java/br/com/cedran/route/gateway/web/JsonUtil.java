package br.com.cedran.route.gateway.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JsonUtil {

    @SneakyThrows
    public static String toJson(Object object) {
        return new ObjectMapper().writeValueAsString(object);
    }
}
