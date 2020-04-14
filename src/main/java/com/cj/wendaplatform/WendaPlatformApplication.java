package com.cj.wendaplatform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(value = "com.cj.wendaplatform.mapper.UserMapper")
@SpringBootApplication
public class WendaPlatformApplication {

    public static void main(String[] args) {

        SpringApplication.run(WendaPlatformApplication.class, args);
    }

}
