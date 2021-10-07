package study.test.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.test.domain.User;
import study.test.repository.UserRepository;

import java.util.Optional;

@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Long join(User user){
        return userRepository.save(user);
    }

    public User findOne(Long userId){
        Optional<User> findUser=userRepository.findById(userId);

        return findUser.orElseThrow(()->{
                    throw new IllegalArgumentException();
                }
        );
    }

    public void removeAll(){
        userRepository.removeAll();
    }

    public Long getCountInDb(){
        return userRepository.getCount();
    }
}
