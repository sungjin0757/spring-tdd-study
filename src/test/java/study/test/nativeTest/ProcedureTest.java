package study.test.nativeTest;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

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
