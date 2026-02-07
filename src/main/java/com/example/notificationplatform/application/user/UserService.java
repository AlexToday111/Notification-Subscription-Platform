package com.example.notificationplatform.application.user;

import com.example.notificationplatform.domain.user.User;
import com.example.notificationplatform.infrastructure.persistence.user.UserRepository;
import com.example.notificationplatform.application.user.command.CreateUserCommand;
import com.example.notificationplatform.application.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User create(CreateUserCommand cmd) {
        if (cmd == null) throw new IllegalArgumentException("command is null");
        if (cmd.email() == null || cmd.email().isBlank()) throw new IllegalArgumentException("email is blank");
        if (cmd.name() == null || cmd.name().isBlank()) throw new IllegalArgumentException("name is blank");

        String email = cmd.email().trim().toLowerCase();
        String name = cmd.name().trim();

        userRepository.findByEmail(email).ifPresent(u -> {
            throw new IllegalStateException("Email already exists: " + email);
        });

        User user = new User(email, name);
        return userRepository.save(user);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public User get(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
    }
}
