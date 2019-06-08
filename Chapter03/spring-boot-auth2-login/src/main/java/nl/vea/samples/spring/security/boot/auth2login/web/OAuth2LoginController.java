package nl.vea.samples.spring.security.boot.auth2login.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OAuth2LoginController {

    @Value("${application.test.fake-password}")
    private String testPassword;

    @Value("${application.test.enc-fake-password}")
    private String decryptedTestPassword;
//Caused by: java.lang.IllegalArgumentException: Could not resolve placeholder 'enc-fake-password'
// in value "${enc-fake-password}"
    private final Logger logger = LoggerFactory.getLogger(OAuth2LoginController.class);
    @GetMapping("/")
    public String index(Model model,
                        @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient,
                        @AuthenticationPrincipal OAuth2User oauth2User) {
        logger.debug("Called with testPassword {}", testPassword);
        logger.debug("Called with decryptedTestPassword {}", decryptedTestPassword);
        model.addAttribute("userName", oauth2User.getName());
        model.addAttribute("clientName", authorizedClient.getClientRegistration().getClientName());
        model.addAttribute("userAttributes", oauth2User.getAttributes());
        return "index";
    }
}
