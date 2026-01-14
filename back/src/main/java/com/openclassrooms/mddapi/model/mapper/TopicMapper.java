package com.openclassrooms.mddapi.model.mapper;

import org.springframework.lang.NonNull;

import com.openclassrooms.mddapi.model.dto.TopicDto;
import com.openclassrooms.mddapi.model.entity.Topic;

public class TopicMapper {

    @NonNull
    public static TopicDto toDto(Topic topic) {
        TopicDto topicDto = new TopicDto();

        topicDto.setId(topic.getId());
        topicDto.setTitle(topic.getTitle());
        topicDto.setDescription(topic.getDescription());
        topicDto.setCreatedAt(topic.getCreatedAt());
        topicDto.setUpdatedAt(topic.getUpdatedAt());

        return topicDto;
    }
}