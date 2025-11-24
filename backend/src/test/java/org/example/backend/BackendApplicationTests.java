package org.example.backend;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BackendApplicationTests {

    @Test
    void checkDefaultUserPasswords() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        assertTrue(
                encoder.matches("123456", "$2a$10$RQxpVuZGgnfe5lgXnkc/1ODooBk6I5avnhRqRrnE5mVaRqGBIwXtu"),
                "teacher001 默认密码不是 123456");

        assertTrue(
                encoder.matches("123456", "$2a$10$TXbPiA6ESkp2YZhyWF1lbej5oCV6eRBNZmPXK8BxvGoH0xx733X7O"),
                "student001 默认密码不是 123456");

        assertTrue(
                encoder.matches("123456", "$2a$10$PyvK2LJJFijeFcIOtgIC2.t.sMLGGHYn4dwqkDzAin.pbPV0gl4.S"),
                "student002 默认密码不是 123456");
    }
    
    @Test
void genPasswordFor123456() {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    String hash = encoder.encode("123456");
    // 故意让测试失败，把 hash 打在失败信息里
    org.junit.jupiter.api.Assertions.fail("123456 的哈希是： " + hash);
}

}