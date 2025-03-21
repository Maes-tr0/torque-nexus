package ua.torque.nexus.feature.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

@Configuration
public class ThymeleafConfig {

    @Bean
    public SpringResourceTemplateResolver emailTemplateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setPrefix("classpath:/templates/email/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(false);
        return templateResolver;
    }

    @Bean
    public TemplateEngine templateEngine(SpringResourceTemplateResolver emailTemplateResolver) {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.addTemplateResolver(emailTemplateResolver);
        return engine;
    }
}
