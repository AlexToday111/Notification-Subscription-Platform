package com.example.notificationplatform.application.notification;

import com.example.notificationplatform.domain.notification.Notification;
import com.example.notificationplatform.infrastructure.persistence.notification.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void listByUser_nullUserId_throws() {
        assertThrows(IllegalArgumentException.class, () -> notificationService.listByUser(null));
    }

    @Test
    void listByUser_returnsRepositoryResults() {
        UUID userId = UUID.randomUUID();
        List<Notification> expected = List.of(mock(Notification.class));

        when(notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId)).thenReturn(expected);

        List<Notification> actual = notificationService.listByUser(userId);

        assertSame(expected, actual);
        verify(notificationRepository).findByUser_IdOrderByCreatedAtDesc(userId);
    }
}
