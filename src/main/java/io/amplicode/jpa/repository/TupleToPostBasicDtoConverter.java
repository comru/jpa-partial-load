package io.amplicode.jpa.repository;

import io.amplicode.jpa.model.Post_;
import io.amplicode.jpa.projection.PostBasicDto;
import org.springframework.core.convert.converter.Converter;

import java.util.Map;

//@Component
public class TupleToPostBasicDtoConverter implements Converter<Map<?, ?>, PostBasicDto> {

    @Override
    public PostBasicDto convert(Map<?, ?> map) {
        return new PostBasicDto(
                (Long) map.get(Post_.ID),
                (String) map.get(Post_.SLUG),
                (String) map.get(Post_.TITLE)
        );
    }
}
