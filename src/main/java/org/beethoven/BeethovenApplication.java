package org.beethoven;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BeethovenApplication {

    public static void main(String[] args) {
        System.setProperty("jna.library.path", "libs");
        SpringApplication.run(BeethovenApplication.class, args);
    }

}
