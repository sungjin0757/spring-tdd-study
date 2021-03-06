## TDD λ°©λ²λ‘  STUDY

***

### π μμνκΈ°μ μμ

<span style="color:lightpink; font-weight:bold"> JUnitμ κΈ°λ₯λ³΄λ€λ νμ€νΈ μ£Όλ κ°λ° λ°©λ²λ‘ μ μ΄μ μ λ§μΆκ² μ΅λλ€.</span>

`service`κ° `repository`λ₯Ό μμ‘΄νκ³  μλ ννμ μ½λλ₯Ό νμ©νμ¬ μ€λͺ νκ² μ΅λλ€.
`service`μ `repository`μ μ½λλ λ°κ³Ό κ°μ΅λλ€.

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

### π νμ€νΈλ?
- μ€νλ§μ΄ κ°μ‘°νκ³  κ°μΉλ₯Ό λκ³  μλ κ².
- μ½λλ₯Ό νμ ν  μ μκ² ν΄μ£Όκ³ , λ³νμ μ μ°νκ² λμ²ν  μ μμ.
- λ€μν κΈ°μ μ νμ©νλ λ°©λ²μ μ΄ν΄νκ³  κ²μ¦νκ³ , μ€μ μ μ μ©νλ λ°©λ²μ μ΅νλ λ° ν¨κ³Όμ μΌλ‘ μ¬μ© κ°λ₯.

#### βοΈ νμ€νΈμ μ μ©μ±
μ½λλ₯Ό μλ‘ μμ± & λ¦¬ν©ν λ§νμμ λ, κΈ°λ₯μ΄ μλνλλ‘ μνν¨μ λ³΄μ₯ν΄ μ€ μ μλ λ°©λ²μλλ€.
κ²°κ΅­ λ΄κ° μμνκ³  μλνλ λλ‘ μ½λκ° μ νν λμνλ μ§λ₯Ό νμ μμΌμ£Όλ κ³Όμ μ΄λ©°, μ΄λ° κ³Όμ μ ν΅ν΄ λλ²κΉκΉμ§ μμν  μ μμ΅λλ€.

#### βοΈ μΉμ ν΅ν νμ€νΈ λ°©λ²μ λ¬Έμ μ 

μλ₯Ό λ€μ΄, νμ¬ `repository`μ μμ μ΄ μ΄λ£¨μ΄μ Έ μ΄λ₯Ό νμ€νΈνκΈ° μν΄μ , μΉ μ νλ¦¬μΌμ΄μμ μλ²μ λ°°μΉν λ€,
μΉ νλ©΄μ λμ νΌμ μ΄κ³ , κ°λ±μ μλ ₯νμ¬ μ½λκ° μλν λλ‘ λμνλ μ§ νμΈνλ λ°©λ²μ΄ λλ€μ μλλ€.

λ³΄κΈ°μλ, λ³ λ¬Έμ κ° μμ΄ λ³΄μΌ μλ μμ§λ§, μκ°μ ν΄λ³΄λ©΄ μ½λμ μμ λ¨μκ° μμ λ κ²μ νμ€νΈ νκΈ° μνμ¬ λλ¬΄ λ§μ κ³μΈ΅μ λ‘μ§μ΄
νμνκ³  μ¬μ§μ΄λ `thymeleaf`μ κ°μ μλ²μ¬μ΄λ λ λλ§ λ·°λ₯Ό ν΅ν νλ©΄κΉμ§ λ§λ€μ΄μ§κ³  λμμΌ νμ€νΈλ₯Ό μμν  μ μλ€λ μ μλλ€.

λν, λλ¬΄ λ§μ μ½λκ° μ°Έμ¬νλ€λ³΄λ μ μ νμ€νΈνκ³  μΆμ κΈ°λ₯μ νμ€νΈλ₯Ό νμ§ λͺ»νκ³  λ€λ₯Έ λΆλΆμμ μ€λ₯κ° λ  μ λμμ΅λλ€.

**λ°λΌμ, μ΄λ₯Ό μ΄λ»κ² κ°μ ν κΉλΌλ μκ°μ΄ νμν©λλ€.**

#### βοΈ λ¨μ νμ€νΈ

μΉμ ν΅ν νμ€νΈ λ°©λ²μ λ¬Έμ μμ λ³΄μ¨λ€μνΌ, νμ€νΈνκ³ μ νλ λμμ΄ λͺννλ€λ©΄ κ·Έ λμμλ§ μ§μ€ν΄μ νμ€νΈνλ κ²μ΄ Bestμλλ€.
λ°λΌμ, νμ€νΈλ <span style="color:red; font-weight:bold;">κ°λ₯νλ©΄ μμ λ¨μ</span>λ‘ μͺΌκ°μ μνλμ΄μΌ ν©λλ€.

**μ΄λ₯Ό λ¨μ νμ€νΈλΌκ³  λΆλ¦λλ€.**

**λ¨μ νμ€νΈ**
- λ²μκ° λ± μ ν΄μ§ κ²μ μλ
- ν¬κ²λ μ¬μ©μ κ΄λ¦¬ κΈ°λ₯μ λͺ¨λ ν΅νμ΄μ 
- μκ²λ λ©μλ νλλ§ κ°μ§κ³ 

λ¬Όλ‘ , λλ‘λ λ¨μλ€μ΄ ν΅ν©λμ΄ μ€ν λμμ λ λ¬Έμ κ° λ°μν  μ μμΌλ κΈ΄ κ³Όμ μ νμ€νΈλ λ¬Όλ‘  νμνκ² λ©λλ€.
νμ§λ§, λ¨μ νμ€νΈλ₯Ό μ λλ‘ κ΅¬νν΄λμ§ μκ³ μ κΈ΄ νμ€νΈλ§μ μννμμ λ λ°μνλ λ¬Έμ μ μμΈμ μ ννκ² μ°ΎκΈ° μ΄λ ΅κ² λ©λλ€.

μ΄λ° μν©μ λλΉν΄μλ λ¨μ νμ€νΈμ μ€μμ±μ λΆκ°λ©λλ€.

**μ΄μ  μ λ¦¬**
- κ°λ°μκ° μλν λλ‘ μ½λκ° μ μνλλμ§ λ°λ‘λ°λ‘ νμΈν  μ μμ΅λλ€.
- λ¨μκ° μλ μ νλ¦¬μΌμ΄μ μμ²΄λ₯Ό νμ€νΈν  λ, λ°κ²¬λλ μ€λ₯λ₯Ό μ’ λ λͺννκ² μ κ·Ό κ°λ₯ν©λλ€.

**μλμν νμ€νΈ**

λ¨μ νμ€νΈλ₯Ό κ΅¬μ±ν  λλ μ½λμ κ²μ¦ μμμ΄ μλμ μΌλ‘ μνλλλ‘ κ΅¬μ±λμ΄μΌν©λλ€.

νμ€νΈλ₯Ό μλμνν¨μΌλ‘μ, λ²κ±°λ‘μ΄ μλ ₯κ³Όμ  λ° μλ²λ₯Ό λμμΌνλ μμμ μ€μΌ μ μκ³  κ·Έ κ²°κ³Ό νμ€νΈμ μκ° λ¨μΆμ΄ μ΄λ£¨μ΄μ§κ² λ©λλ€.

μ΄λ₯Ό ν΅νμ¬, μ€μ  μ΄μμ€μΈ μλ²μ μ½λκ° μμ λμμ λλΌκ³  νμ¬λ λΉ λ₯΄κ² νμ€νΈλ₯Ό μννμ¬ λ²κ·Έκ° μλμ§ μλμ§λ₯Ό λΉ λ₯΄κ² νμΈ λν ν  μ μμ΅λλ€.

***

### π JUnit νμ€ν νλ μμν¬

JUnitμ λ§μ νμ€νΈλ₯Ό κ°λ¨ν μ€νν μ μκ³ , νμ€νΈμ κ²°κ³Όλ₯Ό μ’ν©ν΄μ λ³Ό μ μκ³ , νμ€νΈκ° μ€ν¨ν κ³³μ λΉ λ₯΄κ² μ°Ύμ μ μλ κΈ°λ₯μ κ°μΆ
νμ€ν μ§μ νλ μμν¬ μλλ€.

> μ¬κΈ°μ νλ μμν¬λ?
> 
> νλ μμν¬λ κ°λ°μκ° λ§λ  ν΄λμ€μ λν μ μ΄ κΆνμ λκ²¨λ°μμ μ£Όλμ μΌλ‘ μ νλ¦¬μΌμ΄μμ νλ¦μ μ μ΄ν©λλ€.

λ°λΌμ, κ°λ°μλ νμ€νΈ μ½λλ₯Ό μλμν€λ Main()λ©μλλ νμμκ³  μ€λΈμ νΈλ₯Ό λ§λ€μ΄μ μ€νμν€λ μ½λλν λ§λ€ νμκ° μμ΅λλ€.

JUnit νμ€νΈ μ½λλ₯Ό μμ±νκΈ° μν΄μλ λ¨μ νμ€νΈλ₯Ό νκΈ°μν ν΄λμ€λ₯Ό λ§λ  λ€μ, λ©μλ μμ `@Test` Annotationμ λΆμ¬μ£Όμλ©΄ λ©λλ€.

<span style="color:red; font-weight:bold;">ex)</span>

```java
public class Test{
    @Test
    void νμ€νΈ(){
        //λ©μλ μ΄λ¦μ νκΈλ νμ©!!
    }
}
```

### π νμ€νΈ μ½λ μμ±

**μ’μ νμ€νΈ μ½λλ₯Ό μμ±νκΈ° μν λ°©λ²**
1. νμ€νΈ κ²°κ³Όμ μΌκ΄μ±
    - DBμ λ°μ΄ν°λ₯Ό INSERTνλ μ½λλ₯Ό μκ°ν΄ λ΄μλ€. νμ€νΈλ₯Ό μννκ³  λμλ DBμ νμ΄λΈ λ°μ΄ν°λ₯Ό λͺ¨λ μ­μ μμΌμ€μΌν©λλ€.
   λ§μΌ νμ€νΈ μ, μ€λ³΅λ μ λ³΄κ° INSERTλκ² λλ€λ©΄ μμΈκ° λ°μν  κ²μλλ€. μ¦, μ±κ³΅ν΄μΌ λ§λν νμ€νΈκ° μ€ν¨ν  μλ μκ² λ©λλ€.
   λ°λΌμ, νμ€νΈμ κ²°κ³Όλ ν­μ λμΌνλλ‘ κ΅¬μ±ν΄μΌν©λλ€.
    - λ¬Όλ‘ , μ μ μ½λλ `JPA`λ₯Ό μ¬μ©νμκΈ° λλ¬Έμ, κΈ°λ³Έν€κ° μ€λ³΅λ  νλ₯ μ κ±°μ μ κ³ , νμ€νΈ μ€μλ νμ€νΈν Rollbackμ΄ κ°λ₯νμ§λ§ 
   μΌλ¨μ λͺμμ μΌλ‘ λ³΄λκ² μ’λ€κ³  μκ°λ€μ΄ `removeAll()`μ΄λΌλ λ©μλλ₯Ό μ μνμμ΅λλ€.

```java
@Override
@Modifying(flushAutomatically = true,clearAutomatically = true)
public void removeAll() {
em.createQuery("delete from User u").executeUpdate();
}
```
2. λμΌν κ²°κ³Όλ₯Ό λ³΄μ₯νλ νμ€νΈ
    - λ¨μ νμ€νΈλ μ½λκ° λ°λμ§ μλλ€λ©΄ λ§€λ² μ€νν  λλ§λ€ λμΌν νμ€νΈ κ²°κ³Όλ₯Ό μ»μ μ μμ΄μΌν©λλ€.
    - DBμ λ°μ΄ν°λ₯Ό νμ€νΈνκΈ° μν΄μλ μ μ μ΄λ€ νμ€νΈ μμμ΄ μ΄λ£¨μ΄μ‘λμ§ νμ€ν μμ§ λͺ»νλ―λ‘ νμ€νΈ μνμ΄ λλ λ€ DBμ λ°μ΄ν°λ₯Ό μ§μ°λ κ²λ³΄λ€λ
   νμ€νΈλ₯Ό μμνκΈ° μ μ νμ€νΈ μ€νμ λ¬Έμ κ° λμ§μλ μνλ₯Ό λ§λ€μ΄μ£Όλ κ²μ΄ λ μ’μ λ°©λ²μ΄λΌκ³  ν  μ μμ΅λλ€.
- 1λ²κ³Ό 2λ²μ ν΅νμ¬ μνλκ³  μλ νμ€νΈλ μΈλΆ νκ²½μ μν₯μ λ°μ§ λ§μμΌνλ€λ κ²μ μ μ μμ΅λλ€.
3. ν¬κ΄μ μΈ νμ€νΈ
   - νμ€νΈλ₯Ό μλ§λλ κ²λ λ¬Έμ μ΄μ§λ§, μ±μ μμ΄ νμ€νΈλ₯Ό λ§λλ λ°λμ λ¬Έμ μλ μ½λλ₯Ό νμ€νΈκ° μ±κ³΅νλλ‘ λ§λλ κ²μ λμ±λ λ¬Έμ κ° λ©λλ€.
   ν κ°μ§ κ²°κ³Όλ§ κ²μ¦νλ€κ±°λ, μ€λ₯κ° λλ λ°μ΄ν°μ νμ€νΈλ₯Ό νμ§ μλ κ²μ μνν  μ μμ΅λλ€.

μμ μ‘°κ±΄λ€μ νμ©νμ¬ νμ€νΈ μ½λλ₯Ό μμ±ν΄λ³΄κ² μ΅λλ€.

```java
@SpringBootTest
//μ€νλ§ μ»¨νμ΄λμμ κ΄λ¦¬νλ Beanμ νμ©νκΈ° μν΄ μ¬μ©
public class TestV1 {

    @Autowired
    UserService userService;

    @Test
    @DisplayName("Add & Find νμ€νΈ")
    void λ°μ΄ν°_μΆκ°_νμ€νΈ(){

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
    @DisplayName("μμΈ νμ€νΈ")
    void μμΈ_νμ€νΈ(){
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

>νμ€νΈ μ½λ κ°λ¨ν 
> `Assertions.assertThat(findUser3.getName()).isEqualTo(user3.getName());`
> μ κ°μ μ½λλ findUser3.getName()κ³Ό user3.getName()μ κ°μ λΉκ΅νκ² λ©λλ€.
> 
> κ°μ΄ λμΌνλ€λ©΄ νμ€νΈλ μ±κ³΅ν  κ²μ΄κ³ , κ°μ΄ λμΌνμ§ μλ€λ©΄ νμ€νΈλ μ€ν¨ν©λλ€.
> 
> `org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,()->{
userService.findOne(1l);`
});
> 
> `userService.findOne(1l)`μ μ€ννμ λ, μ΄λ€ μμΈκ° λμ Έμ§λμ§λ₯Ό κ²μ¦νλ μ½λμλλ€. 

**μμ, μ€λͺν μ‘°κ±΄λ€μ νμ©νμ¬ νμ€νΈ μ½λλ₯Ό μμ±ν  μ μμμ΅λλ€.**
- κ° νμ€νΈ λ©μλλ₯Ό μμν  λμ `removeAll()`μ ν΅νμ¬ DBλΌλ μΈλΆνκ²½μ μ μΈμν€κ³  λ¨μ νμ€νΈλ§μ κ²μ¦ν  μ μμμ΅λλ€.
- νλμ λ°μ΄ν°λ§ κ²μ¦νλ κ²μ΄ μλ μ¬λ¬ λ°μ΄ν°λ₯Ό μΆκ°νμ¬ νμ€νΈλ₯Ό κ²μ¦νμκ³ , μμΈλ₯Ό λμ§λ λΆμ μ μΈ μΌμ΄μ€λ₯Ό ν΅νμ¬
μμΈκ° μΆ©λΆν μ λμ Έμ§λ κ² λν νμΈν  μ μμμ΅λλ€.

#### βοΈ μΆκ°λ‘, ν¬κ΄μ μΈ νμ€νΈ
ν¬κ΄μ μΈ νμ€νΈλ₯Ό λ§λλ μ΄μ λ λ―Έλμ μΉλͺμ μΈ μν©μ λ°μμ μλ°©μν¬λλ° μμ΄μ κ°μ₯ μ€μν μ­ν μ ν©λλ€.

λͺ¨λ  μν©μ λνμ¬ νμ€νΈλ₯Ό ν΄λ³΄μ§ μμλ€λ©΄ λμ€μ λ¬Έμ κ° λ°μνμ λ μμΈμ μ°ΎκΈ° νλ€μ΄μ κ³ μνκ² λ μ§λ λͺ¨λ₯Έλ€λ κ²μ λ»ν©λλ€.
μ¦, ν­μ μ±κ³΅νλ νμ€νΈλ§μ λ§λ€μ§ λ§μλ κ²μ΄ μ£Όλ λͺ©μ μλλ€.

μλ₯Ό λ€λ©΄) λ°μ΄ν°λ₯Ό μΆκ°νλ λ©μλλ₯Ό λ§λ€ λ, λ©μλκ° μ μΆκ°λμλμ§λ§ κ²μ¦νλ κ²μ΄ μλ μ€λ³΅λ λ°μ΄ν°λ₯Ό μΆκ°νμ λμ λ¬Έμ , νΉμ
DBμ μ μ₯λμ΄μμ§ μμ Idμ λ°μ΄ν°λ₯Ό κ°μ Έμμ λμ λ¬Έμ  λ±λ± μμΈμ μΈ μν© λν κ²μ¦μ΄ νμνλ€λ κ²μλλ€.

### π TDD (Test Driven Development)

κ°λ΅ν, TDDλ νμ€νΈ μ£Όλ κ°λ°μ΄λΌ λΆλ¦¬λ©° νμ€νΈ μ½λλ₯Ό λ¨Όμ  λ§λ€κ³ , νμ€νΈλ₯Ό μ±κ³΅νκ² ν΄μ£Όλ μ½λλ₯Ό μμ±νλ λ°©μμ κ°λ°μλλ€.

μλ₯Όλ€μ΄, 
```java
@Test
@DisplayName("μμΈ νμ€νΈ")
void μμΈ_νμ€νΈ(){
    userService.removeAll();
    org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,()->{
        userService.findOne(1l);
    });
}
```
μ΄μ κ°μ νμ€νΈ μ½λλ₯Ό λ¨Όμ  λ§λ  λ€μ, νμ€νΈ μ½λμ λ§μΆ° κΈ°λ₯μ κ°λ°νλ κ²μλλ€.

μ¦, μμ νμ€νΈ μ½λλ₯Ό λ¨Όμ  λ§λ  λ€μ

```java
public User findOne(Long userId){
    Optional<User> findUser=userRepository.findById(userId);

    return findUser.orElseThrow(()->{
        throw new IllegalArgumentException();
    }
    );
}
```
μ΄λ κ² κΈ°λ₯μ νλ μ½λλ₯Ό μμ±ν΄μ€λ€ νμ€νΈλ₯Ό μ€ννμ¬ κ°λ°ν μ½λλ₯Ό κ²μ¦νλ κ²μλλ€.

**TDDμ μ₯μ **
- μΆκ°νκ³  μΆμ κΈ°λ₯μ νμ€νΈ μ½λλ‘ νννμ¬, μΌμ’μ κΈ°λ₯ μ€κ³ μ­ν μ ν΄μ€λλ€.
- κΈ°λ₯μ κ°μ§ μ½λλ₯Ό κ΅¬νν λ€ μμ±λμ΄μλ νμ€νΈ μ½λλ₯Ό λμνμ¬ λΉ λ₯΄κ² κ²μ¦μ΄ κ°λ₯ν©λλ€.
- νμ€νΈκ° μ€ν¨νκ² λλ€λ©΄, μ€κ³ν λλ‘ μ½λκ° μ μμ μΌλ‘ λ§λ€μ΄μ§ μμμμ λ»νλ©° νμ€νΈμ ν΅κ³ΌνκΈ° μνμ¬ κ³μμ μΌλ‘ μ½λλ₯Ό 
λ€λ¬μ΄ λκ° μ μμ΅λλ€.

#### βοΈ νμ€νΈ μ£Όλ κ°λ°
TDDλ νμ€νΈλ₯Ό κ²μ¦νλ©° κ°λ°νλ μ₯μ μ κ·Ήλν μμΌμ£Όλ κ°λ°λ‘ μλλ€.

TDDμ κΈ°λ³Έ μμΉμ "μ€ν¨ν νμ€νΈλ₯Ό μ±κ³΅μν€κΈ° μν λͺ©μ μ΄ μλ μ½λλ λ§λ€μ§ μλλ€"λΌκ³  ν©λλ€. μ΄ μμΉμ ν΅ν΄ κ°λ°λ μ½λλ λͺ¨λ νμ€νΈκ°
κ²μ¦λ μνλΌκ³  ν  μ μμ΅λλ€.

**TDDκ° μ¬μ©λμ΄μΌ νλ μ΄μ **
- μ½λλ₯Ό λ§λ€κ³  λμ μκ°μ΄ μ§λλ©΄ νμ€νΈ μ½λλ₯Ό λ§λ€κΈ° κ·μ°?μ μ μμ΅λλ€. λν, μ½λμ μμ΄ μ΄λ§μ΄λ§νλ€λ©΄, μ΄λ»κ² νμ€νΈλ₯Ό ν΄μΌν μ§ κ°λ μμ‘νκ³  κ²°κ΅­μλ
νμ€νΈλ₯Ό μν΄λ²λ¦΄μλ μμ΅λλ€. TDDλ μμ νμ€νΈλ₯Ό λ¨Όμ  λ§λ€κ³  νμ€νΈλ₯Ό μ±κ³΅νλ μ½λλ₯Ό λ§λ€μ΄ λ΄κΈ° λλ¬Έμ νμ€νΈλ₯Ό κΌΌκΌΌν λ§λ€ μ μμ΅λλ€.
- μ½λμ λν νΌλλ°±μ λΉ λ₯΄κ² λ°μ μ μμ΅λλ€.
- λ¨μ νμ€νΈλ₯Ό λ§λλκ²μ μ λ¦¬ν©λλ€.
- μ½λλ₯Ό λ§λ€κΈ° μ  λ¨Έλ¦Ώμμμλ§ λ³΅μ‘νκ² μκ°νλ μ½λ μ€κ³λ₯Ό νμ€νΈ μ½λλ₯Ό ν΅ν΄ μ©μ΄νκ² μ€κ³ν  μ μμ΅λλ€.
- κ°λ°ν μ½λμ μ€λ₯λ₯Ό λΉ λ₯΄κ² λ°κ²¬ν  μ μμ΅λλ€.

#### βοΈ νμ€νΈ μ½λ κ°μ 
μΆκ°λ‘, μμμ μμ±ν νμ€νΈ μ½λλ₯Ό κ°μ μμΌλ³΄λλ‘ νκ² μ΅λλ€.

λ¨Όμ , μμ±νλ νμ€νΈ μ½λλ₯Ό μ΄ν΄ λ΄μλ€.
```java
@SpringBootTest
//μ€νλ§ μ»¨νμ΄λμμ κ΄λ¦¬νλ Beanμ νμ©νκΈ° μν΄ μ¬μ©
public class TestV1 {

    @Autowired
    UserService userService;

    @Test
    @DisplayName("Add & Find νμ€νΈ")
    void λ°μ΄ν°_μΆκ°_νμ€νΈ(){

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
    @DisplayName("μμΈ νμ€νΈ")
    void μμΈ_νμ€νΈ(){
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

μμ μ½λμμ λ°μ΄ν°_μΆκ°_νμ€νΈλ₯Ό λ³΄μλ©΄ 

` User user1=createUser("hong","1234");`, 

`User user2=createUser("hong1","123");`, 

`User user3=createUser("hong12","1233");`

μ κ°μ΄ μ€λ³΅λ μ½λκ° μμ΅λλ€. JUnitμ΄ μ κ³΅νλ κΈ°λ₯μ νμ©ν΄ λ΄μλ€.

μ€λ³΅λλ μ½λλ₯Ό λ©μλ μμ λ£μ λ€μ `@BeforeEach` Annotationμ λΆμ¬μ£Όλ©΄ λ©λλ€.

**κ°μ λ μ½λ**
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
    @DisplayName("V2 νμ€νΈ")
    void V2_νμ€νΈ(){

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
    @DisplayName("μμΈ νμ€νΈ")
    void μμΈ_νμ€νΈ(){
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

**κ·Έλ λ€λ©΄, JUnitμ μ΄λ»κ² νμ€νΈλ₯Ό μ€νμν€λ κ²μΌκΉμ??**
1. νμ€νΈ ν΄λμ€μμ @Testκ° λΆμ λ©μλλ₯Ό λͺ¨λ μ°Ύμ΅λλ€.
2. @BeforeAllκ° λΆμ λ©μλκ° μμΌλ©΄ μ€νν©λλ€.
3. νμ€νΈ ν΄λμ€μ μ€λΈμ νΈλ₯Ό νλ λ§λ­λλ€.
4. @BeforeEach κ° λΆμ λ©μλκ° μμΌλ©΄ μ€νν©λλ€.
5. @Testκ° λΆμ λ©μλλ₯Ό νΈμΆνκ³  νμ€νΈ κ²°κ³Όλ₯Ό μ μ₯ν©λλ€.
6. @AfterEach κ° λΆμ λ©μλκ° μμΌλ©΄ μ€νν©λλ€.
7. λλ¨Έμ§ νμ€νΈμ λνμ¬ 3~6λ²μ μμμ λ°λ³΅ν©λλ€.
8. @AfterAllκ° λΆμ λ©μλκ° μμΌλ©΄ μ€νν©λλ€.
9. λͺ¨λ  νμ€νΈμ κ²°κ³Όλ₯Ό μ’ν©ν΄μ λλ €μ€λλ€.

μ¬κΈ°μ μ μν΄μΌν  μ μ κ° νμ€νΈ λ©μλλ₯Ό μ€νν  λλ§λ€ νμ€νΈ ν΄λμ€μ μ€λΈμ νΈλ₯Ό μλ‘ λ§λ λ€λ μ μλλ€.
**μ¦, @Testκ° λΆμ λ©μλκ° 3κ°λΌλ©΄ 3κ°μ μ€λΈμ νΈκ° λ§λ€μ΄μ§λ κ²μλλ€.**

JUnitμ΄ μ νμ€νΈ λ©μλ νλνλ λ§λ€ μ€λΈμ νΈλ₯Ό λ§λ€κΉμ??

κ° νμ€νΈκ° μλ‘ μν₯μ μ£Όμ§ μκ³  λλ¦½μ μΌλ‘ μ€νλ¨μ νμ€ν λ³΄μ¬μ£ΌκΈ° μν΄ λ§€λ² μλ‘μ΄ μ€λΈμ νΈλ₯Ό λ§λ­λλ€.

νμ€νΈ λ©μλμ μΌλΆμμλ§ κ³΅ν΅μ μΌλ‘ μ¬μ©λλ μ½λκ° μλ κ²½μ°λ λ©μλλ₯Ό λΆλ¦¬νκ³  λ©μλλ₯Ό μ§μ  νΈμΆν΄ μ¬μ©νλλ‘ λ§λλ νΈμ΄ λ λ«μ΅λλ€.
νμ§λ§, νμ€νΈκ° λ§κ³  μ¬μ©λ  μ€λΈμ νΈκ° κ±°μ κ³΅ν΅μ μΌλ‘ μκ°λ λμλ ν©μ΄μ Έμλ κ²λ³΄λ€ @BeforeEachλ @BeforeAll(λ©μλκ° staticμ΄μ΄μΌν¨)λ±μ μ¬μ©νλ κ²μ΄ μ’μ΅λλ€.

### π λ§μ§λ§μΌλ‘ 

**νλμ ν΄λμ€μ 3κ°μ νμ€νΈ λ©μλκ° μμ λ, `@BeforeAll`, `@BeforeEach`, `@AfterAll`, `@AfterEach`μ μ€νμμλ₯Ό λ³΄κ² μ΅λλ€.**

**μμ€μ½λ**
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
    void νμ€νΈ_1(){
        log.info("Test1 Start!");
    }

    @Test
    void νμ€νΈ_2(){
        log.info("Test2 Start!");
    }

    @Test
    void νμ€νΈ_3(){
        log.info("Test3 Start!");
    }
}
```

**μ€ν κ²°κ³Ό**

<img width="651" alt="αα³αα³αα΅α«αα£αΊ 2021-11-18 αα©αα₯α« 1 33 53" src="https://user-images.githubusercontent.com/56334761/142242182-2eb3fc2a-0e73-43bc-814e-808326f0abaf.png">

μ€ν κ²°κ³Όμμ νμΈνμ€ μ μλ―μ΄, `@BeforeEach`, `@AfterEach`λ κ°κ° νμ€νΈ λ©μλμ μ€ν μ ,νλ‘ μ€νλλ κ²μ νμΈνμ¬ λ³΄μ€ μ μμ΅λλ€.

λν, `@BeforeAll`, `@AfterAll`μ μ μ²΄ νμ€νΈμ μ€ν μ ,νλ‘ νλ² μ© μ€νλλ κ²μ νμΈν  μ μμ΅λλ€.
