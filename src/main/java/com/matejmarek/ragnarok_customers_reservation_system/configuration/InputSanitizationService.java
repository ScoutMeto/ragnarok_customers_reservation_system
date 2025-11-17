package com.matejmarek.ragnarok_customers_reservation_system.configuration;

import org.apache.commons.lang3.StringEscapeUtils;
import org.owasp.html.PolicyFactory;
import org.springframework.stereotype.Service;

@Service
public class InputSanitizationService {

    private final PolicyFactory textPolicy;

    public InputSanitizationService() {
        this.textPolicy = SanitizationPolicies.TEXT_ONLY;
    }

    public String sanitizeTextOnly(String input) {
        if (input == null) return null;
        return textPolicy.sanitize(input);
    }

    /** Vrátí PLAIN TEXT (bez tagů, bez entit) – ideální pro textContent */
    public String sanitizeTextOnlyPlain(String input) {
        if (input == null) return null;
        String htmlSafe = textPolicy.sanitize(input);
        return StringEscapeUtils.unescapeHtml4(htmlSafe);
    }
    
}