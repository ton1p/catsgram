package ru.yandex.practicum.catsgram.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@Data
@Builder
@EqualsAndHashCode(of = {"email"})
public class User {
    Long id;
    String username;
    String email;
    String password;
    Instant registrationDate;
}
