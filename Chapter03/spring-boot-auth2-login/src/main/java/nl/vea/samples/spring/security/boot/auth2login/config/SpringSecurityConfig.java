package nl.vea.samples.spring.security.boot.auth2login.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /*super.configure(http);*/
        //See https://www.baeldung.com/spring-security-5-oauth2-login
        /*
            To prevent
            DEBUG o.s.s.w.a.ExceptionTranslationFilter - Access is denied (user is anonymous);
            redirecting to authentication entry point
            org.springframework.security.access.AccessDeniedException: Access is denied
            based on
            o.s.s.w.a.AnonymousAuthenticationFilter - Populated SecurityContextHolder with anonymous token:
            'org.springframework.security.authentication.AnonymousAuthenticationToken@294334ac: Principal: anonymousUser;
            Credentials: [PROTECTED]; Authenticated: true;
            Details: org.springframework.security.web.authentication.WebAuthenticationDetails@957e:
            RemoteIpAddress: 127.0.0.1; SessionId: null; Granted Authorities: ROLE_ANONYMOUS'
         */
        http
          /*  .authorizeRequests()
            .anyRequest()
            .hasAnyRole("ANONYMOUS")
        .and()*/
            .authorizeRequests()
            .anyRequest()
            .authenticated()
        .and()
            .oauth2Client()
        .and()
            .oauth2Login();

    }
}
