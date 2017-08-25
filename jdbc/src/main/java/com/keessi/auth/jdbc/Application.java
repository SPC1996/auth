package com.keessi.auth.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.sql.DataSource;

@SpringBootApplication
public class Application {
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final DataSource dataSource;

    @Autowired
    public Application(@Qualifier("dataSource") DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Configuration
    @EnableResourceServer
    protected static class ResourceServer extends ResourceServerConfigurerAdapter {
        private final TokenStore tokenStore;

        @Autowired
        public ResourceServer(TokenStore tokenStore) {
            this.tokenStore = tokenStore;
        }

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
            resources.tokenStore(tokenStore);
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                    .antMatchers("/api/**")
                    .authenticated()
                    .anyRequest()
                    .permitAll();
        }
    }

    @Configuration
    @EnableAuthorizationServer
    protected static class OAuth2Config extends AuthorizationServerConfigurerAdapter {
        private final AuthenticationManager auth;
        private final DataSource dataSource;
        private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        @Autowired
        public OAuth2Config(AuthenticationManager auth, @Qualifier("dataSource") DataSource dataSource) {
            this.auth = auth;
            this.dataSource = dataSource;
        }

        @Bean
        public JdbcTokenStore tokenStore() {
            return new JdbcTokenStore(dataSource);
        }

        @Bean
        public AuthorizationCodeServices authorizationCodeServices() {
            return new JdbcAuthorizationCodeServices(dataSource);
        }

        @Override
        public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
            security.passwordEncoder(passwordEncoder);
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            endpoints.authorizationCodeServices(authorizationCodeServices())
                    .authenticationManager(auth)
                    .tokenStore(tokenStore())
                    .approvalStoreDisabled();
        }

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients.jdbc(dataSource)
                    .passwordEncoder(passwordEncoder)
            //初始化时使用下列注释代码，数据库中存在数据时需要注释下列代码
                    .withClient("my_trusted_client")
                    .authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit")
                    .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
                    .scopes("read", "write", "trust")
                    .accessTokenValiditySeconds(600)
                    .refreshTokenValiditySeconds(6000)
                    .and()
                    .withClient("my_client_with_registered_redirect")
                    .authorizedGrantTypes("authorization_code")
                    .authorities("ROLE_CLIENT")
                    .scopes("read", "trust")
                    .redirectUris("http://www.baidu.com")
                    .accessTokenValiditySeconds(600)
                    .refreshTokenValiditySeconds(6000)
                    .resourceIds("oauth2-resource")
                    .and()
                    .withClient("my_client_with_secret")
                    .authorizedGrantTypes("client_credentials", "password")
                    .authorities("ROLE_CLIENT")
                    .scopes("read")
                    .accessTokenValiditySeconds(600)
                    .refreshTokenValiditySeconds(6000)
                    .resourceIds("oauth2-resource")
                    .secret("secret");
        }
    }

    @Autowired
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(passwordEncoder)
                .withUser("spc")
                .password(passwordEncoder.encode("199602"))
                .roles("USER", "ACTUATOR");
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
