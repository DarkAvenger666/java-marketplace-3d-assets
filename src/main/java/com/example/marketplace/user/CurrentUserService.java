package com.example.marketplace.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {
    private final UserRepository users;   // Optional<User> findByName(String name);

    public User get() {
        // TODO: заменить на SecurityContextHolder???, когда появится авторизация
        return users.findByName("system").orElseThrow();
    }
}