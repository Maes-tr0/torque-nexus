package ua.torque.nexus.feature.notification.mail.template;

import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Component
public class EmailTemplateProvider {

    private final TemplateEngine templateEngine;

    public EmailTemplateProvider(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String build(String templateName, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);
        return templateEngine.process(templateName, context);
    }
}
