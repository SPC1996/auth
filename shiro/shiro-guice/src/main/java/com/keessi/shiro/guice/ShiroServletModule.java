package com.keessi.shiro.guice;

import com.google.inject.Provides;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.name.Names;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.config.ConfigurationException;
import org.apache.shiro.config.Ini;
import org.apache.shiro.guice.web.ShiroWebModule;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;

import javax.inject.Singleton;
import javax.servlet.ServletContext;
import java.net.MalformedURLException;
import java.net.URL;

public class ShiroServletModule extends ShiroWebModule {
    private final ServletContext servletContext;

    public ShiroServletModule(ServletContext servletContext) {
        super(servletContext);
        this.servletContext = servletContext;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void configureShiroWeb() {
        bindConstant().annotatedWith(Names.named("shiro.loginUrl")).to("/login.jsp");
        try {
            bindRealm().toConstructor(IniRealm.class.getConstructor(Ini.class));
        } catch (NoSuchMethodException e) {
            addError("Could not locate proper constructor for IniRealm.", e);
        }
        addFilterChain("/login.jsp", AUTHC);
        addFilterChain("/logout", LOGOUT);
        addFilterChain("/account/**", AUTHC);
        addFilterChain("/remoting/**", AUTHC, config(ROLES, "b2bClient"), config(PERMS, "remote:invoke:lan,wan"));
    }

    @Provides
    @Singleton
    Ini loadShiroIni() throws MalformedURLException {
        URL iniUrl = servletContext.getResource("/WEB-INF/shiro.ini");
        return Ini.fromResourcePath("url:" + iniUrl.toExternalForm());
    }

    @Override
    protected void bindWebSecurityManager(AnnotatedBindingBuilder<? super WebSecurityManager> bind) {
        try {
            String cipherKey = loadShiroIni().getSectionProperty("main", "securityManager.rememberMeManager.cipherKey");

            DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
            CookieRememberMeManager rememberMeManager = new CookieRememberMeManager();
            rememberMeManager.setCipherKey(Base64.decode(cipherKey));
            securityManager.setRememberMeManager(rememberMeManager);
            bind.toInstance(securityManager);
        } catch (MalformedURLException e) {
            throw new ConfigurationException("securityManager.rememberMeManager.cipherKey must be set in shiro.ini.");
        }
    }
}
