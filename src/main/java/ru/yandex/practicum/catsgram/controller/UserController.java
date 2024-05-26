package ru.yandex.practicum.catsgram.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {
    Map<Long, User> users = new HashMap<>();

    public List<String> getEmails() {
        return users.values().stream().map(User::getEmail).toList();
    }

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        if (user.getEmail() == null) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }
        if (getEmails().contains(user.getEmail())) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        if (user.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        User result;

        if (users.containsKey(user.getId())) {
            result = users.get(user.getId());

            if (!user.getEmail().equals(result.getEmail()) && getEmails().contains(user.getEmail())) {
                throw new DuplicatedDataException("Этот имейл уже используется");
            }

            if (user.getUsername() != null) {
                result.setUsername(user.getUsername());
            }

            if (user.getEmail() != null) {
                result.setEmail(user.getEmail());
            }

            if (user.getPassword() != null) {
                result.setPassword(user.getPassword());
            }

            return result;
        }

        result = user;
        result.setId(getNextId());
        users.put(user.getId(), result);
        return result;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
