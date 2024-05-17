package com.jvpars.codetip.security.jwt;

import com.jvpars.codetip.service.api.AppUserService;
import com.jvpars.codetip.utils.DocumentService;
import com.jvpars.codetip.utils.MyArgUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class JwtTokenFilter extends GenericFilterBean {

    @Autowired
    AppUserService appUserService;


    public JwtTokenFilter() {
    }

    private JwtTokenProvider jwtTokenProvider;

    JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
            throws IOException, ServletException {


        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;
        String utr = request.getRequestURI();


        //log.info("RequestURI >>>>>>>> " +utr);
        Enumeration headerNames = request.getHeaderNames();
      while (headerNames.hasMoreElements()) {
            String headerName = (String) headerNames.nextElement();
            log.info("headerName: {} , headerValue :{}" , headerName, request.getHeader(headerName));
        }

        if (request.getMethod().equals("OPTIONS")) {
            //log.info("option calling");
            response.setStatus(HttpServletResponse.SC_OK);
            filterChain.doFilter(req, res);
            return;
        }

        if(utr.contains("/error")) {
            filterChain.doFilter(req, res);
            return;
        }

        if(utr.contains("/account")) {
            filterChain.doFilter(req, res);
            return;
        }

        if(utr.contains("/dl")) {
            filterChain.doFilter(req, res);
            return;
        }

        if (utr.contains("h2")) {
            //log.info("h2 chaining..");
            filterChain.doFilter(req, res);
            return;
        }

        if (utr.contains("error")) {
            log.info("error chaining..");
            filterChain.doFilter(req, res);
            return;
        }


        if (utr.contains("codeTipSocket")) {
            //log.info("web socket request " +utr);
            extractToken(request);
            filterChain.doFilter(req, res);
            return;
        }

        else {
            //log.info("normal request");
                String token = jwtTokenProvider.resolveToken((HttpServletRequest) req);
                if (token != null){
                    //log.info("token : " + token);
                        if(jwtTokenProvider.validateToken(token)) {
                            Authentication auth = jwtTokenProvider.getAuthentication(token);
                            if (auth != null) {
                                SecurityContextHolder.getContext().setAuthentication(auth);
                               // log.info("auth : " + auth);
                            }
                        }
                        else {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token.");
                        }
                }
                else{
                       response.sendError(HttpServletResponse.SC_FORBIDDEN);
                       return;
                }
                //log.info("chaining");
                filterChain.doFilter(req, res);

        }
    }

    //.........
    private final static String AUTHENTICATION_PARAMETER = "token";
    private boolean extractToken( HttpServletRequest request ) {
        //log.info("extractToken");
        if (request.getQueryString() != null) {

            String qs = request.getQueryString();
            qs = qs.replaceAll("%20", " ");
            //log.info(">>>jw>>>"+qs);

            String[] pairs = qs.split("&");
            for (String pair : pairs) {
                String[] pairTokens = pair.split("=");
                if (pairTokens.length == 2) {
                    if (AUTHENTICATION_PARAMETER.equals(pairTokens[0])) {
                           String token = pairTokens[1];

                            Authentication auth = jwtTokenProvider.getAuthentication(token);
                            SecurityContextHolder.getContext().setAuthentication(auth);
                            //log.debug("Using credentials: " + pairTokens[1]);
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }



