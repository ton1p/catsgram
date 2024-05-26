package ru.yandex.practicum.catsgram.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@Builder
@EqualsAndHashCode(of = {"id"})
public class Image {
    Long id;
    long postId;
    String originalFileName;
    String filePath;
}
