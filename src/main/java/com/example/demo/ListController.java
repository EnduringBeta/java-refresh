package com.example.demo;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/// This controller is for experimenting with lists and maps
@RestController
public class ListController {
    // Using CopyOnWriteArrayList for thread safety
    private final List<User> users = new CopyOnWriteArrayList<>(Arrays.asList(
        new User(0, "Alice", 30, "Engineer"),
        new User(1, "Bob", 25, "Designer"),
        new User(2, "Charlie", 35, "Engineer"),
        new User(3, "Diana", 28, "Manager")
    ));

    private final HashMap<Long, User> userMap = new HashMap<>();

    // Constructor to initialize the userMap
    public ListController() {
        // Add all initial users to map
        for (User user : users) {
            userMap.put(user.getId(), user);
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

    @GetMapping("/users")
    public List<User> getUser() {
        return userMap.values().stream()
                .collect(Collectors.toList());
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable long id) {
        return userMap.get(id);
    }

    @PutMapping("/users/")
    public User putUser(@RequestBody(required = true) User user) {
        // Check if user already exists
        if (userMap.containsKey(user.getId())) {
            // Update existing user
            User existingUser = userMap.get(user.getId());
            existingUser.setName(user.getName());
            existingUser.setAge(user.getAge());
            existingUser.setProfession(user.getProfession());
            return existingUser;
        } else {
            long newId = userMap.size() + 1; // Generate new ID
            user.setId(newId);

            // Add new user
            users.add(user);
            userMap.put(user.getId(), user);
            return user;
        }
    }

    @DeleteMapping("/users/{id}")
    public Boolean deleteUser(@PathVariable long id) {
        User user = userMap.remove(id);
        if (user != null) {
            users.remove(user);
            return true;
        }
        return false;
    }

    @PostMapping("/upsert")
    public Boolean upsertUser(@RequestBody(required = true) User user) {
        Optional<User> existing = users.stream().filter((User u) -> u.getName().equals(user.getName())).findFirst();

        // Add user if name not already present
        if (!existing.isPresent()) {
            users.add(user);
            userMap.put(user.getId(), user);
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
            userMap.put(existingUser.getId(), existingUser);
        }

        return isChanged;
    }
}
