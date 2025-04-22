package com.lzy.mall.tiny;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(basePackages = {"com/lzy/mall/tiny/dao"})
@SpringBootApplication
public class MallTinyElasticsearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallTinyElasticsearchApplication.class, args);
    }

}
