package com.lims;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * LIMS 实验室信息管理系统启动主类
 */
@SpringBootApplication(scanBasePackages = "com.lims")
@MapperScan("com.lims.**.mapper")
public class LimsApplication {

    public static void main(String[] args) {
        SpringApplication.run(LimsApplication.class, args);
    }
}
