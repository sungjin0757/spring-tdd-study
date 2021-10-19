package study.test.learningtest;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

public class JUnitTestV2 {
    static Set<JUnitTestV2> testObject=new HashSet<>();

    @Test
    @DisplayName("1번째 오브젝트 테스트")
    void test1(){
        Assertions.assertThat(testObject).doesNotContain(this);
        testObject.add(this);
    }

    @Test
    @DisplayName("2번째 오브젝트 테스트")
    void test2(){
        Assertions.assertThat(testObject).doesNotContain(this);
        testObject.add(this);
    }

    @Test
    @DisplayName("3번째 오브젝트 테스트")
    void test3(){
        Assertions.assertThat(testObject).doesNotContain(this);
        testObject.add(this);
    }
}
