package com.app.whiteboard.config;

import com.app.whiteboard.crdt.StrokeCRDT;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class BeansConfig {
    @Bean
    public StrokeCRDT strokeCRDT() {
        return new StrokeCRDT();
    }
}
