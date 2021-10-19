package study.test.learningtest;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashSet;
import java.util.Set;

@SpringBootTest
@ContextConfiguration
public class JUnitTestV3 {

    @Autowired
    TestApplicationContext applicationContext;

    static Set<JUnitTestV3> testObject=new HashSet<>();
    static TestApplicationContext contextObject=null;

    @Test
    @DisplayName("1번째 오브젝트 테스트")
    void test1(){
        Assertions.assertThat(testObject).doesNotContain(this);
        Assertions.assertThat(contextObject==null || contextObject==this.applicationContext).isEqualTo(true);
        testObject.add(this);
        contextObject=applicationContext;
    }

    @Test
    @DisplayName("2번째 오브젝트 테스트")
    void test2(){
        Assertions.assertThat(testObject).doesNotContain(this);
        Assertions.assertThat(contextObject==null || contextObject==this.applicationContext).isEqualTo(true);
        testObject.add(this);
        contextObject=applicationContext;
    }

    @Test
    @DisplayName("3번째 오브젝트 테스트")
    void test3(){
        Assertions.assertThat(testObject).doesNotContain(this);
        Assertions.assertThat(contextObject==null || contextObject==this.applicationContext).isEqualTo(true);
        testObject.add(this);
        contextObject=applicationContext;
    }
}
