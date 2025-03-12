package org.beethoven;

import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@NacosPropertySource(dataId = "beethoven", autoRefreshed = true)
public class BeethovenApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeethovenApplication.class, args);
    }

}
