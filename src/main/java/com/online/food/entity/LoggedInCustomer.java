package com.online.food.entity;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@Scope(value = "singleton")
public class LoggedInCustomer {
    private final Set<String> users = new HashSet<>();

    public Set<String> getUsers() {
        return users;
    }

    public void addUser(String username) {
        users.add(username);
    }

    public void removeUser(String username) {
        users.remove(username);
    }
}
