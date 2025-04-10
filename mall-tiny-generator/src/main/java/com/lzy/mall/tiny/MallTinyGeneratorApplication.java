package com.lzy.mall.tiny;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan({"com.lzy.mall.tiny.mbg.mapper","com.lzy.mall.tiny.dao"})
public class MallTinyGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallTinyGeneratorApplication.class, args);
    }

}
