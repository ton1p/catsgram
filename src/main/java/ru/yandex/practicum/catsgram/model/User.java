package ru.yandex.practicum.catsgram.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
@EqualsAndHashCode(of = {"email"})
public class User {
    Long id;
    String username;
    String email;
    String password;
    Instant registrationDate;
}
