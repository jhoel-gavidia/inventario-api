package com.motorepuestos.inventario.util;

import tools.jackson.databind.DatabindException;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JsonUtil {

    private final ObjectMapper objectMapper;

    public String convertir(Object objeto) {
        if (objeto == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(objeto);
        } catch (DatabindException e) {
            log.warn("No se pudo convertir el objeto a JSON: {}", e.getMessage());
            return "{\"error\":\"No se pudo serializar el objeto\"}";
        }
    }
}