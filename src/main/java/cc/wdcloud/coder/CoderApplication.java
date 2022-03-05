package cc.wdcloud.coder;

import com.thetransactioncompany.cors.CORSFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@Configuration
//@EnableApolloConfig
//@EnableSwagger2
@EnableConfigurationProperties
public class CoderApplication {
    public static void main(String[] args) {
        SpringApplication.run(CoderApplication.class, args);
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(new CORSFilter());
        bean.setName("cors");
        bean.addUrlPatterns("/*");
        return bean;
    }

}
