package repositories;

import entities.Session;

import java.time.LocalDateTime;

public interface SessionRepository {
    void addSession(Long userId, String sessionId, LocalDateTime expireAt);
    Session getSessionById(String sessionId);
}
