package com.EmpTimeHub.config;

import com.EmpTimeHub.constants.EnumConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
@Component
@ConfigurationProperties(prefix = "mail")
public class MailTemplateConfig {

    private Map<String, String> templates;

    public String getTemplate(EnumConstants.MailTemplateType type) {
        return templates.get(type.name());
    }

    public Map<String, String> getTemplates() {
        return templates;
    }

    public void setTemplates(Map<String, String> templates) {
        this.templates = templates;
    }
}
