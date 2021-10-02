package study.test.repository;

import study.test.domain.User;

import java.util.Optional;

public interface UserRepository {
    Long save(User user);
    Optional<User> findById(Long userId);
    void removeOne(Long userId);
    void removeAll();
    Long getCount();
}
