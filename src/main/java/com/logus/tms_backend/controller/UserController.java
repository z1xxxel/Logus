package com.logus.tms_backend.controller;

import com.logus.tms_backend.model.User;
import com.logus.tms_backend.model.UserRole;
import com.logus.tms_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * 🛠️ Обновить данные пользователя (только Admin)
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id,
                                           @RequestParam String name,
                                           @RequestParam String email,
                                           @RequestParam UserRole role,
                                           @RequestParam Long adminId) {
        User admin = userService.getUserById(adminId);
        if (admin.getRole() != UserRole.Admin) {
            throw new RuntimeException("Доступ запрещен: только для администраторов");
        }

        User updatedUser = userService.updateUser(id, name, email, role);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * 🛠️ Удалить пользователя (только Admin)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id,
                                           @RequestParam Long adminId) {
        User admin = userService.getUserById(adminId);
        if (admin.getRole() != UserRole.Admin) {
            throw new RuntimeException("Доступ запрещен: только для администраторов");
        }

        userService.deleteUser(id, adminId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 🛠️ Получить статистику системы (только Admin)
     */
    @GetMapping("/admin/stats")
    public ResponseEntity<Map<String, Long>> getAdminStats(@RequestParam Long adminId) {
        User admin = userService.getUserById(adminId);
        if (admin.getRole() != UserRole.Admin) {
            throw new RuntimeException("Доступ запрещен: только для администраторов");
        }

        return ResponseEntity.ok(userService.getAdminStats());
    }
}