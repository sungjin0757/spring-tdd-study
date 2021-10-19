package study.test.learningtest;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class JUnitTestV1 {
    static JUnitTestV1 testObject;

    @Test
    @DisplayName("1번째 오브젝트 테스트")
    void test1(){
        Assertions.assertThat(this).isNotEqualTo(testObject);
        testObject=this;
    }

    @Test
    @DisplayName("2번째 오브젝트 테스트")
    void test2(){
        Assertions.assertThat(this).isNotEqualTo(testObject);
        testObject=this;
    }

    @Test
    @DisplayName("3번째 오브젝트 테스트")
    void test3(){
        Assertions.assertThat(this).isNotEqualTo(testObject);
        testObject=this;
    }
}
