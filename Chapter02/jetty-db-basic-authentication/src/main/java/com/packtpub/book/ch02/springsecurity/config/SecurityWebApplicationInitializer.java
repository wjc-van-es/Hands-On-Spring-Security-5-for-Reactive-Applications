package com.packtpub.book.ch02.springsecurity.config;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

/**
 * This will take care of initializing the servlet filter for security without using web.xml
 */
public class SecurityWebApplicationInitializer
        extends AbstractSecurityWebApplicationInitializer {
    // Nothing to be added here
}
