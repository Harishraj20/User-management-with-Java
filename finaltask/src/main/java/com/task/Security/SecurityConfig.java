package com.task.Security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.task.Service.MyUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    protected static final Logger logger = LogManager.getLogger();

    @Autowired
    private MyUserDetailsService userDetailsService;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    private final LogoutHandlerCookie logoutHandlerCookie;



    @Autowired
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, LogoutHandlerCookie logoutHandlerCookie) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.logoutHandlerCookie = logoutHandlerCookie;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        logger.info("Indise configure Method for Logout!");
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/css/**", "/javascript/**", "/login").permitAll() //It makes the resource to be open [Dont need Authorization/Authentication] if not used it says Access Denied for those URL!
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login") // Redirect to this URL for Unauthenticated Users // If not added shows 403 Forbidden error!
                .permitAll().and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .logout()
                .logoutUrl("/users/logout").addLogoutHandler(logoutHandlerCookie)
                .invalidateHttpSession(true)
                .logoutSuccessUrl("/login")
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); //Spring security dont use session for authenticiation of the user. if not used even when token is deleted manually from the cookies we can able to navigate to further page due to the session being handled Hence set session as Stateless to prevent such scenario!
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        logger.info("Inside Authentication Provider method!");
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(new BCryptPasswordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();

    }

}
