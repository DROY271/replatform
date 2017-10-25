package org.superbiz.moviefun;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

import java.util.Collections;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ServletRegistrationBean moviefunServletBean(ActionServlet actionServlet) {
        ServletRegistrationBean srb = new ServletRegistrationBean();
        srb.setServlet(actionServlet);
        srb.setUrlMappings(Collections.singletonList("/moviefun"));
        return srb;
    }
}
