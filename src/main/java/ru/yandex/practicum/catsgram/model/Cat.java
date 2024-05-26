package ru.yandex.practicum.catsgram.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Cat {
    String color;
    int age;
}
