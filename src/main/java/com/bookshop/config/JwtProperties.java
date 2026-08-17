package com.bookshop.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * Secret key used to sign JWT.
     */
    private String secret;

    /**
     * Token expiration time in milliseconds.
     */
    private long expiration;

}