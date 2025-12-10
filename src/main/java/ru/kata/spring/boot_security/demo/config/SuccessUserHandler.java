package ru.kata.spring.boot_security.demo.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Logger;

@Component
public class SuccessUserHandler implements AuthenticationSuccessHandler {

    private static final String TEMPLATE_MESSAGE_AUTH = "User '%s' logged in with '%s'. Redirect '%s'";
    private static final Logger LOGGER = Logger.getLogger(SuccessUserHandler.class.getName());

    @Value("${app.url.userInfo}")
    private String userUrl;

    @Value("${app.url.admin}")
    private String adminUrl;

    @Value("${app.url.default}")
    private String defaultUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String username = authentication.getName();
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        if (roles.contains("ROLE_ADMIN")) {
            LOGGER.info(String.format(TEMPLATE_MESSAGE_AUTH, username, "ROLE_ADMIN", "/admin"));
            response.sendRedirect(adminUrl);
        } else if (roles.contains("ROLE_USER")) {
            LOGGER.info(String.format(TEMPLATE_MESSAGE_AUTH, username, "ROLE_USER", "/users/info"));
            response.sendRedirect(userUrl);
        } else {
            LOGGER.info(String.format(TEMPLATE_MESSAGE_AUTH, username, "NO ROLE", "/"));
            response.sendRedirect(defaultUrl);
        }
    }
}