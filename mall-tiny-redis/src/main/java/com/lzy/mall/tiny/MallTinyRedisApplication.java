package com.lzy.mall.tiny;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com/lzy/mall/tiny/mbg/mapper")
public class MallTinyRedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallTinyRedisApplication.class, args);
    }

}
