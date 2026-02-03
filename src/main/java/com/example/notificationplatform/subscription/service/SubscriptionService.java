package com.example.notificationplatform.subscription.service;

import com.example.notificationplatform.subscription.domain.Subscription;
import com.example.notificationplatform.subscription.repo.SubscriptionRepository;
import com.example.notificationplatform.subscription.service.command.CreateSubscriptionCommand;
import com.example.notificationplatform.shared.error.NotFoundException;
import com.example.notificationplatform.user.domain.User;
import com.example.notificationplatform.user.repo.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SubscriptionService {

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(UserRepository userRepository,
                               SubscriptionRepository subscriptionRepository) {
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Transactional
    public Subscription create(CreateSubscriptionCommand cmd) {
        if (cmd == null) throw new IllegalArgumentException("command is null");
        if (cmd.userId() == null) throw new IllegalArgumentException("userId is null");
        if (cmd.eventType() == null) throw new IllegalArgumentException("eventType is null");
        if (cmd.channel() == null) throw new IllegalArgumentException("channel is null");
        if (cmd.destination() == null || cmd.destination().isBlank()) throw new IllegalArgumentException("destination is blank");

        User user = userRepository.findById(cmd.userId())
                .orElseThrow(() -> new NotFoundException("User not found: " + cmd.userId()));

        String destination = cmd.destination().trim();
        if (!cmd.channel().isValidDestination(destination)) {
            throw new IllegalArgumentException("Invalid destination for channel " + cmd.channel());
        }

        Subscription entity = new Subscription(
                user,
                cmd.eventType(),
                cmd.channel(),
                destination
        );

        return subscriptionRepository.save(entity);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Subscription> listByUser(UUID userId) {
        if (userId == null) throw new IllegalArgumentException("userId is null");
        return subscriptionRepository.findByUserId(userId);
    }
}
