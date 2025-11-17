package com.matejmarek.ragnarok_customers_reservation_system.configuration;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

public final class SanitizationPolicies {

    private SanitizationPolicies() {

    }

    public static final PolicyFactory TEXT_ONLY = new HtmlPolicyBuilder().toFactory();
}
