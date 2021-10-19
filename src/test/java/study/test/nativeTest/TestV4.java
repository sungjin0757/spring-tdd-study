package study.test.nativeTest;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.test.domain.User;
import study.test.repository.UserRepository;
import study.test.repository.UserRepositoryImpl;
import study.test.service.UserService;

import javax.persistence.EntityManager;

@Transactional
@Slf4j
@SpringBootTest
public class TestV4 {

    @Autowired
    EntityManager em;

    UserService userService;
    UserRepository userRepository;

    User user1;
    User user2;
    User user3;

    @BeforeEach
    void setUp(){
        this.user1=createUser("hong","1234");
        this.user2=createUser("hong1","1234");
        this.user3=createUser("hong12","1234");

        this.userRepository=new UserRepositoryImpl(em);
        this.userService=new UserService(userRepository);

        log.info("userService = {}",this.userService);
        log.info("this = {} ",this);
    }

    @Test
    @DisplayName("V4 테스트")
    void V4_테스트(){

        userService.removeAll();
        Assertions.assertThat(userService.getCountInDb()).isEqualTo(0);

        User user1=createUser("hong","1234");
        Long saveId1 = userService.join(user1);
        Assertions.assertThat(userService.getCountInDb()).isEqualTo(1);

        User user2=createUser("hong1","123");
        Long saveId2 = userService.join(user2);
        Assertions.assertThat(userService.getCountInDb()).isEqualTo(2);

        User user3=createUser("hong12","1233");
        Long saveId3 = userService.join(user3);
        Assertions.assertThat(userService.getCountInDb()).isEqualTo(3);

        User findUser1 = userService.findOne(saveId1);
        Assertions.assertThat(findUser1.getName()).isEqualTo(user1.getName());
        Assertions.assertThat(findUser1.getPassword()).isEqualTo(user1.getPassword());

        User findUser2 = userService.findOne(saveId2);
        Assertions.assertThat(findUser2.getName()).isEqualTo(user2.getName());
        Assertions.assertThat(findUser2.getPassword()).isEqualTo(user2.getPassword());

        User findUser3 = userService.findOne(saveId3);
        Assertions.assertThat(findUser3.getName()).isEqualTo(user3.getName());
        Assertions.assertThat(findUser3.getPassword()).isEqualTo(user3.getPassword());

    }

    @Test
    @DisplayName("예외 테스트")
    void 예외_테스트(){
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,()->{
            userService.findOne(1l);
        });
    }


    private User createUser(String name,String password){
        return User.createUser()
                .name(name)
                .password(password)
                .build();
    }
}
