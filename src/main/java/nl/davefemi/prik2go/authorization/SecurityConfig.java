package nl.davefemi.prik2go.authorization;

import nl.davefemi.prik2go.exceptions.AuthorizationException;
import nl.davefemi.prik2go.service.OAuth2Service;
import org.springframework.boot.web.servlet.filter.OrderedFormContentFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.concurrent.TimeoutException;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurity(HttpSecurity http, JWTAuthFilter jwtAuthFilter, OrderedFormContentFilter formContentFilter) throws Exception {
        http
                .securityMatcher("/private/locations/**")
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(form->form.disable())
                .oauth2Login(withDefaults())
                .httpBasic(basic -> basic.disable());
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain jwtFilterChain(HttpSecurity http, JWTAuthFilter jWTAuthFilter) throws Exception {
        http.securityMatcher("/auth/**", "/private/oauth2/request/**")
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/auth/login", "/auth/create-user").permitAll()
                                .anyRequest().authenticated())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jWTAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
        ;
        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain oauthGoogle(HttpSecurity http, OAuth2Service oAuth2Service) throws Exception {
        http
                .securityMatcher( "/oauth2/**")
                .csrf(csfr -> csfr.disable())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers(
                                        "/oauth2/code/google/**",
                                        "/oauth2/login/google",
                                        "/oauth2/request/**")
                                .permitAll()
                                .anyRequest().authenticated()
                                )
                .oauth2Login(o -> o
                        .authorizationEndpoint(a -> a.baseUri("/oauth2/authorization"))
                        .redirectionEndpoint(r -> r.baseUri("/oauth2/code/*"))
                        .successHandler((req, res, auth) -> {
                    boolean result = true;
                    try {
                        oAuth2Service.validateOidcUser((OidcUser) auth.getPrincipal(),
                                (String) req.getSession(false).getAttribute("userId"),
                                (String) req.getSession(false).getAttribute("request"));
                    } catch (AuthorizationException | TimeoutException e) {
                        System.out.println(e.getMessage());
                        result = false;
                    }
                            req.getSession(false).removeAttribute("userId");
                    req.getSession(false).removeAttribute("request");
                    res.sendRedirect("/oauth2/status/google?login=" + result);
                }));
        return http.build();
    }
}