package com.example.demo;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/// This controller is for experimenting with lists and maps
@RestController
public class ListController {
    // Using CopyOnWriteArrayList for thread safety
    private final List<User> users = new CopyOnWriteArrayList<>(Arrays.asList(
        new User("Alice", 30, "Engineer"),
        new User("Bob", 25, "Designer"),
        new User("Charlie", 35, "Engineer"),
        new User("Diana", 28, "Manager")
    ));

    private final HashMap<String, User> userMap = new HashMap<>();

    // Constructor to initialize the userMap
    public ListController() {
        // Add all initial users to map
        for (User user : users) {
            userMap.put(user.getName(), user);
        }
    }

    @GetMapping("/list")
    public List<User> listUsers(@RequestParam(required = false) String professionFilter) {
        if (professionFilter != null && !professionFilter.isEmpty()) {
            return users.stream()
                    .filter(user -> user.getProfession().equalsIgnoreCase(professionFilter))
                    .collect(Collectors.toList());
        }
        return users;
    }

    @GetMapping("/user")
    public User getUser(@RequestParam(required = true) String name) {
        return userMap.get(name);
    }

    @PostMapping("/upsert")
    public Boolean upsertUser(@RequestBody(required = true) User user) {
        Optional<User> existing = users.stream().filter((User u) -> u.getName().equals(user.getName())).findFirst();

        // Add user if name not already present
        if (!existing.isPresent()) {
            users.add(user);
            userMap.put(user.getName(), user);
            return true;
        }

        // Update user otherwise
        User existingUser = existing.get();

        // Use reflection to compare all fields
        Boolean isChanged = false;
        Class<User> userClazz = User.class;
        for (Field field : userClazz.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object newValue = field.get(user);
                Object oldValue = field.get(existingUser);
                if (newValue != null && !newValue.equals(oldValue)) {
                    field.set(existingUser, newValue);
                    isChanged = true;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if (isChanged) {
            // Update the user in the map
            userMap.put(existingUser.getName(), existingUser);
        }

        return isChanged;
    }
}
