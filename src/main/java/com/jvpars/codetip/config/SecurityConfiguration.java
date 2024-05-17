package com.jvpars.codetip.config;


import com.jvpars.codetip.security.jwt.JwtConfigurer;
import com.jvpars.codetip.security.jwt.JwtTokenFilter;
import com.jvpars.codetip.security.jwt.JwtTokenProvider;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    final JwtTokenProvider jwtTokenProvider;


    public SecurityConfiguration(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

//    @Bean
//    public FilterRegistrationBean jwtFilter() {
//        final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
//        registrationBean.setFilter(new JwtTokenFilter() );
//        registrationBean.addUrlPatterns("/api/*");
//        registrationBean.setOrder(0);
//        return registrationBean;
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.cors().configurationSource(new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedHeaders(Collections.singletonList("*"));
                config.setAllowedMethods(Collections.singletonList("*"));
                config.addAllowedOrigin("*");
                config.setAllowCredentials(true);
                return config;
            }
        });


        http.httpBasic()
                .disable()
                .csrf()
                .disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authorizeRequests()
                .antMatchers("/account/**").permitAll()
                .antMatchers("/dl/**").permitAll()
                .antMatchers("/file-manager/**").permitAll()
                .antMatchers("/h2console/**").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .apply(new JwtConfigurer(jwtTokenProvider));
                 http.headers().frameOptions().disable();


    }
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }



}
