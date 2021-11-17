## TDD 방법론 STUDY

***

### 📌 시작하기에 앞서

<span style="color:lightpink; font-weight:bold"> JUnit의 기능보다는 테스트 주도 개발 방법론에 초점을 맞추겠습니다.</span>

`service`가 `repository`를 의존하고 있는 형태의 코드를 활용하여 설명 하겠습니다.
`service`와 `repository`의 코드는 밑과 같습니다.

**User.java**
```java
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id @GeneratedValue
    @Column(name="user_id")
    private Long id;

    private String name;
    private String password;

    @Builder(builderMethodName = "createUser")
    public User(String name,String password){
        this.name=name;
        this.password=password;
    }

}
```

**UserRepository.java**
```java
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

```

**UserService.java**

```java
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
```

### 🚀 테스트란?
- 스프링이 강조하고 가치를 두고 있는 것.
- 코드를 확신할 수 있게 해주고, 변화에 유연하게 대처할 수 있음.
- 다양한 기술을 활용하는 방법을 이해하고 검증하고, 실전에 적용하는 방법을 익히는 데 효과적으로 사용 가능.

#### ✏️ 테스트의 유용성
코드를 새로 작성 & 리팩토링하였을 때, 기능이 의도한대로 수행함을 보장해 줄 수 있는 방법입니다.
결국 내가 예상하고 의도했던 대로 코드가 정확히 동작하는 지를 확신시켜주는 과정이며, 이런 과정을 통해 디버깅까지 완수할 수 있습니다.

#### ✏️ 웹을 통한 테스트 방법의 문제점

예를 들어, 현재 `repository`의 수정이 이루어져 이를 테스트하기 위해선, 웹 애플리케이션을 서버에 배치한 뒤,
웹 화면을 띄워 폼을 열고, 값등을 입력하여 코드가 의도한 대로 동작하는 지 확인하는 방법이 대다수 입니다.

보기에는, 별 문제가 없어 보일 수도 있지만, 생각을 해보면 코드의 작은 단위가 수정된 것을 테스트 하기 위하여 너무 많은 계층의 로직이
필요하고 심지어는 `thymeleaf`와 같은 서버사이드 렌더링 뷰를 통한 화면까지 만들어지고 나서야 테스트를 완수할 수 있다는 점입니다.

또한, 너무 많은 코드가 참여하다보니 정작 테스트하고 싶은 기능은 테스트를 하지 못하고 다른 부분에서 오류가 날 수 도있습니다.

**따라서, 이를 어떻게 개선할까라는 생각이 필요합니다.**

#### ✏️ 단위 테스트

웹을 통한 테스트 방법의 문제에서 보셨다시피, 테스트하고자 하는 대상이 명확하다면 그 대상에만 집중해서 테스트하는 것이 Best입니다.
따라서, 테스트는 <span style="color:red; font-weight:bold;">가능하면 작은 단위</span>로 쪼개서 수행되어야 합니다.

**이를 단위 테스트라고 부릅니다.**

**단위 테스트**
- 범위가 딱 정해진 것은 아님
- 크게는 사용자 관리 기능을 모두 통틀어서 
- 작게는 메소드 하나만 가지고

물론, 때로는 단위들이 통합되어 실행 되었을 때 문제가 발생할 수 있으니 긴 과정의 테스트도 물론 필요하게 됩니다.
하지만, 단위 테스트를 제대로 구현해놓지 않고서 긴 테스트만을 수행하였을 때 발생하는 문제의 원인을 정확하게 찾기 어렵게 됩니다.

이런 상황을 대비해서도 단위 테스트의 중요성은 부각됩니다.

**이점 정리**
- 개발자가 의도한 대로 코드가 잘 수행되는지 바로바로 확인할 수 있습니다.
- 단위가 아닌 애플리케이션 자체를 테스트할 때, 발견되는 오류를 좀 더 명확하게 접근 가능합니다.

**자동수행 테스트**

단위 테스트를 구성할 때는 코드의 검증 작업이 자동적으로 수행되도록 구성되어야합니다.

테스트를 자동수행함으로서, 번거로운 입력과정 및 서버를 띄워야하는 작업을 줄일 수 있고 그 결과 테스트의 시간 단축이 이루어지게 됩니다.

이를 통하여, 실제 운영중인 서버의 코드가 수정되었을 때라고 하여도 빠르게 테스트를 수행하여 버그가 있는지 없는지를 빠르게 확인 또한 할 수 있습니다.

***

### 🚀 JUnit 테스팅 프레임워크

JUnit은 많은 테스트를 간단히 실행할수 있고, 테스트의 결과를 종합해서 볼 수 있고, 테스트가 실패한 곳을 빠르게 찾을 수 있는 기능을 갖춘
테스팅 지원 프레임워크 입니다.

> 여기서 프레임워크란?
> 
> 프레임워크는 개발자가 만든 클래스에 대한 제어 권한을 넘겨받아서 주도적으로 애플리케이션의 흐름을 제어합니다.

따라서, 개발자는 테스트 코드를 작동시키는 Main()메소드도 필요없고 오브젝트를 만들어시 실행시키는 코드또한 만들 필요가 없습니다.

JUnit 테스트 코드를 작성하기 위해서는 단위 테스트를 하기위한 클래스를 만든 다음, 메소드 위에 `@Test` Annotation을 붙여주시면 됩니다.

<span style="color:red; font-weight:bold;">ex)</span>

```java
public class Test{
    @Test
    void 테스트(){
        //메소드 이름은 한글도 허용!!
    }
}
```

### 🚀 테스트 코드 작성

**좋은 테스트 코드를 작성하기 위한 방법**
1. 테스트 결과의 일관성
    - DB에 데이터를 INSERT하는 코드를 생각해 봅시다. 테스트를 수행하고 나서는 DB의 테이블 데이터를 모두 삭제시켜줘야합니다.
   만일 테스트 시, 중복된 정보가 INSERT되게 된다면 예외가 발생할 것입니다. 즉, 성공해야 마땅한 테스트가 실패할 수도 있게 됩니다.
   따라서, 테스트의 결과는 항상 동일하도록 구성해야합니다.
    - 물론, 저의 코드는 `JPA`를 사용하였기 때문에, 기본키가 중복될 확률은 거의 적고, 테스트 중에도 테스트후 Rollback이 가능하지만 
   일단은 명시적으로 보는게 좋다고 생각들어 `removeAll()`이라는 메소드를 정의하였습니다.

```java
@Override
@Modifying(flushAutomatically = true,clearAutomatically = true)
public void removeAll() {
em.createQuery("delete from User u").executeUpdate();
}
```
2. 동일한 결과를 보장하는 테스트
    - 단위 테스트는 코드가 바뀌지 않는다면 매번 실행할 때마다 동일한 테스트 결과를 얻을 수 있어야합니다.
    - DB의 데이터를 테스트하기 위해서는 전에 어떤 테스트 작업이 이루어졌는지 확실히 알지 못하므로 테스트 수행이 끝난 뒤 DB의 데이터를 지우는 것보다는
   테스트를 시작하기 전에 테스트 실행에 문제가 되지않는 상태를 만들어주는 것이 더 좋은 방법이라고 할 수 있습니다.
- 1번과 2번을 통하여 수행되고 있는 테스트는 외부 환경에 영향을 받지 말아야한다는 것을 알 수 있습니다.
3. 포괄적인 테스트
   - 테스트를 안만드는 것도 문제이지만, 성의 없이 테스트를 만드는 바람에 문제있는 코드를 테스트가 성공하도록 만드는 것은 더욱더 문제가 됩니다.
   한 가지 결과만 검증한다거나, 오류가 나는 데이터의 테스트를 하지 않는 것은 위험할 수 있습니다.

위의 조건들을 활용하여 테스트 코드를 작성해보겠습니다.

```java
@SpringBootTest
//스프링 컨테이너에서 관리하는 Bean을 활용하기 위해 사용
public class TestV1 {

    @Autowired
    UserService userService;

    @Test
    @DisplayName("Add & Find 테스트")
    void 데이터_추가_테스트(){

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
        userService.removeAll();
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
```

>테스트 코드 간단히 
> `Assertions.assertThat(findUser3.getName()).isEqualTo(user3.getName());`
> 와 같은 코드는 findUser3.getName()과 user3.getName()의 값을 비교하게 됩니다.
> 
> 값이 동일하다면 테스트는 성공할 것이고, 값이 동일하지 않다면 테스트는 실패합니다.
> 
> `org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,()->{
userService.findOne(1l);`
});
> 
> `userService.findOne(1l)`을 실행했을 때, 어떤 예외가 던져지는지를 검증하는 코드입니다. 

**앞서, 설명한 조건들을 활용하여 테스트 코드를 작성할 수 있었습니다.**
- 각 테스트 메소드를 시작할 때에 `removeAll()`을 통하여 DB라는 외부환경은 제외시키고 단위 테스트만을 검증할 수 있었습니다.
- 하나의 데이터만 검증하는 것이 아닌 여러 데이터를 추가하여 테스트를 검증하였고, 예외를 던지는 부정적인 케이스를 통하여
예외가 충분히 잘 던져지는 것 또한 확인할 수 있었습니다.

#### ✏️ 추가로, 포괄적인 테스트
포괄적인 테스트를 만드는 이유는 미래에 치명적인 상황의 발생을 예방시크는데 있어서 가장 중요한 역할을 합니다.

모든 상황에 대하여 테스트를 해보지 않았다면 나중에 문제가 발생했을 때 원인을 찾기 힘들어서 고생하게 될지도 모른다는 것을 뜻합니다.
즉, 항상 성공하는 테스트만을 만들지 말자는 것이 주된 목적입니다.

예를 들면) 데이터를 추가하는 메소드를 만들 때, 메소드가 잘 추가되었는지만 검증하는 것이 아닌 중복된 데이터를 추가했을 때의 문제, 혹은
DB에 저장되어있지 않은 Id의 데이터를 가져왔을 때의 문제 등등 예외적인 상황 또한 검증이 필요하다는 것입니다.

### 🚀 TDD (Test Driven Development)

간략히, TDD란 테스트 주도 개발이라 불리며 테스트 코드를 먼저 만들고, 테스트를 성공하게 해주는 코드를 작성하는 방식의 개발입니다.

예를들어, 
```java
@Test
@DisplayName("예외 테스트")
void 예외_테스트(){
    userService.removeAll();
    org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,()->{
        userService.findOne(1l);
    });
}
```
이와 같은 테스트 코드를 먼저 만든 다음, 테스트 코드에 맞춰 기능을 개발하는 것입니다.

즉, 위의 테스트 코드를 먼저 만든 다음

```java
public User findOne(Long userId){
    Optional<User> findUser=userRepository.findById(userId);

    return findUser.orElseThrow(()->{
        throw new IllegalArgumentException();
    }
    );
}
```
이렇게 기능을 하는 코드를 작성해준뒤 테스트를 실행하여 개발한 코드를 검증하는 것입니다.

**TDD의 장점**
- 추가하고 싶은 기능을 테스트 코드로 표현하여, 일종의 기능 설계 역할을 해줍니다.
- 기능을 가진 코드를 구형한 뒤 작성되어있는 테스트 코드를 동작하여 빠르게 검증이 가능합니다.
- 테스트가 실패하게 된다면, 설계한 대로 코드가 정상적으로 만들어지 않았음을 뜻하며 테스트에 통과하기 위하여 계속적으로 코드를 
다듬어 나갈 수 있습니다.

#### ✏️ 테스트 주도 개발
TDD는 테스트를 검증하며 개발하는 장점을 극대화 시켜주는 개발론입니다.

TDD의 기본 원칙은 "실패한 테스트를 성공시키기 위한 목적이 아닌 코드는 만들지 않는다"라고 합니다. 이 원칙을 통해 개발된 코드는 모두 테스트가
검증된 상태라고 할 수 있습니다.

**TDD가 사용되어야 하는 이유**
- 코드를 만들고 나서 시간이 지나면 테스트 코드를 만들기 귀찮을 수 있습니다. 또한, 코드의 양이 어마어마하다면, 어떻게 테스트를 해야할지 감도 안잡히고 결국에는
테스트를 안해버릴수도 있습니다. TDD는 아예 테스트를 먼저 만들고 테스트를 성공하는 코드를 만들어 내기 때문에 테스트를 꼼꼼히 만들 수 있습니다.
- 코드에 대한 피드백을 빠르게 받을 수 있습니다.
- 단위 테스트를 만드는것에 유리합니다.
- 코드를 만들기 전 머릿속에서만 복잡하게 생각했던 코드 설계를 테스트 코드를 통해 용이하게 설계할 수 있습니다.
- 개발한 코드의 오류를 빠르게 발견할 수 있습니다.

#### ⚙️ 테스트 코드 개선
추가로, 위에서 작성한 테스트 코드를 개선시켜보도록 하겠습니다.

먼저, 작성했던 테스트 코드를 살펴 봅시다.
```java
@SpringBootTest
//스프링 컨테이너에서 관리하는 Bean을 활용하기 위해 사용
public class TestV1 {

    @Autowired
    UserService userService;

    @Test
    @DisplayName("Add & Find 테스트")
    void 데이터_추가_테스트(){

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
        userService.removeAll();
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
```

위의 코드에서 데이터_추가_테스트를 보시면 

` User user1=createUser("hong","1234");`, 

`User user2=createUser("hong1","123");`, 

`User user3=createUser("hong12","1233");`

와 같이 중복된 코드가 있습니다. JUnit이 제공하는 기능을 활용해 봅시다.

중복됐던 코드를 메소드 안에 넣은 다음 `@BeforeEach` Annotation을 붙여주면 됩니다.

**개선된 코드**
```java
@SpringBootTest
@Transactional
public class TestV2 {

    @Autowired
    UserService userService;

    User user1;
    User user2;
    User user3;

    @BeforeEach
    void setUp(){
        this.user1=createUser("hong","1234");
        this.user2=createUser("hong1","1234");
        this.user3=createUser("hong12","1234");
    }

    @Test
    @DisplayName("V2 테스트")
    void V2_테스트(){

        userService.removeAll();
        Assertions.assertThat(userService.getCountInDb()).isEqualTo(0);

        Long saveId1 = userService.join(user1);
        Assertions.assertThat(userService.getCountInDb()).isEqualTo(1);

        Long saveId2 = userService.join(user2);
        Assertions.assertThat(userService.getCountInDb()).isEqualTo(2);

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
```

**그렇다면, JUnit은 어떻게 테스트를 실행시키는 것일까요??**
1. 테스트 클래스에서 @Test가 붙은 메소드를 모두 찾습니다.
2. @BeforeAll가 붙은 메소드가 있으면 실행합니다.
3. 테스트 클래스의 오브젝트를 하나 만듭니다.
4. @BeforeEach 가 붙은 메소드가 있으면 실행합니다.
5. @Test가 붙은 메소드를 호출하고 테스트 결과를 저장합니다.
6. @AfterEach 가 붙은 메소드가 있으면 실행합니다.
7. 나머지 테스트에 대하여 3~6번의 작업을 반복합니다.
8. @AfterAll가 붙은 메소드가 있으면 실행합니다.
9. 모든 테스트의 결과를 종합해서 돌려줍니다.

여기서 유의해야할 점은 각 테스트 메소드를 실행할 때마다 테스트 클래스의 오브젝트를 새로 만든다는 점입니다.
**즉, @Test가 붙은 메소드가 3개라면 3개의 오브젝트가 만들어지는 것입니다.**

JUnit이 왜 테스트 메소드 하나하나 마다 오브젝트를 만들까요??

각 테스트가 서로 영향을 주지 않고 독립적으로 실행됨을 확실히 보여주기 위해 매번 새로운 오브젝트를 만듭니다.

테스트 메소드의 일부에서만 공통적으로 사용되는 코드가 있는 경우는 메소드를 분리하고 메소드를 직접 호출해 사용하도록 만드는 편이 더 낫습니다.
하지만, 테스트가 많고 사용될 오브젝트가 거의 공통적으로 생각될때에는 흩어져있는 것보다 @BeforeEach나 @BeforeAll(메소드가 static이어야함)등을 사용하는 것이 좋습니다.

### 📌 마지막으로 

**하나의 클래스에 3개의 테스트 메소드가 있을 때, `@BeforeAll`, `@BeforeEach`, `@AfterAll`, `@AfterEach`의 실행순서를 보겠습니다.**

**소스코드**
```java
@Slf4j
public class ProcedureTest {

    @BeforeAll
    static void beforeAll(){
        log.info("BeforeAll Start!");
    }

    @BeforeEach
    void beforeEach(){
        log.info("BeforeEach Start!");
    }

    @AfterAll
    static void afterAll(){
        log.info("AfterAll Start!");
    }

    @AfterEach
    void afterEach(){
        log.info("AfterEach Start!");
    }

    @Test
    void 테스트_1(){
        log.info("Test1 Start!");
    }

    @Test
    void 테스트_2(){
        log.info("Test2 Start!");
    }

    @Test
    void 테스트_3(){
        log.info("Test3 Start!");
    }
}
```

**실행 결과**

<img width="651" alt="스크린샷 2021-11-18 오전 1 33 53" src="https://user-images.githubusercontent.com/56334761/142242182-2eb3fc2a-0e73-43bc-814e-808326f0abaf.png">

실행 결과에서 확인하실 수 있듯이, `@BeforeEach`, `@AfterEach`는 각각 테스트 메소드의 실행 전,후로 실행되는 것을 확인하여 보실 수 있습니다.

또한, `@BeforeAll`, `@AfterAll`은 전체 테스트의 실행 전,후로 한번 씩 실행되는 것을 확인할 수 있습니다.
