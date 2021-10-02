package study.test.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import study.test.domain.User;

import javax.persistence.EntityManager;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class UserRepositoryImpl implements UserRepository{

    private final EntityManager em;

    @Override
    public Long save(User user) {
        em.persist(user);
        return user.getId();
    }

    @Override
    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(em.find(User.class,userId));
    }

    @Override
    public void removeOne(Long userId) {
        Optional<User> findUser = findById(userId);
        User user=findUser.orElseThrow(
                ()->{
                    throw new IllegalArgumentException();
                }
        );

        em.remove(user);
    }

    @Override
    @Modifying(flushAutomatically = true,clearAutomatically = true)
    public void removeAll() {
        em.createQuery("delete from User u").executeUpdate();
    }

    @Override
    public Long getCount() {
        return em.createQuery("select count(u.id) from User u",Long.class).getSingleResult();
    }
}
