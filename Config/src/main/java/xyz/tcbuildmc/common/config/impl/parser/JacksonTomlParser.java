package xyz.tcbuildmc.common.config.impl.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.toml.TomlMapper;
import org.jetbrains.annotations.Contract;
import xyz.tcbuildmc.common.config.api.parser.Parser;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

public class JacksonTomlParser implements Parser {
    private final TomlMapper mapper;

    @Contract(pure = true)
    public JacksonTomlParser(TomlMapper mapper) {
        this.mapper = mapper;
    }

    public JacksonTomlParser() {
        this(TomlMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build());
    }

    @Override
    public <T> Function<String, T> toObject(Class<T> clazz) {
        return content -> {
            try {
                T instance = this.mapper.readValue(content, clazz);

                if (instance == null) {
                    return clazz.getDeclaredConstructor().newInstance();
                }

                return instance;
            } catch (JsonProcessingException |
                     InvocationTargetException |
                     InstantiationException |
                     IllegalAccessException |
                     NoSuchMethodException e) {

                throw new RuntimeException("Failed to parse.", e);
            }
        };
    }

    @Override
    public <T> Function<T, String> toConfig(Class<T> clazz) {
        return instance -> {
            try {
                return this.mapper.writer().withDefaultPrettyPrinter().writeValueAsString(instance);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to parse.", e);
            }
        };
    }
}