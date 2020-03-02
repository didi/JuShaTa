package com.didiglobal.jushata.launcher;

import com.didiglobal.jushata.springboot.JushataApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JushataLauncher {

    public static void main(String[] args) {
        JushataApplication.run(JushataLauncher.class, args);
    }
}
