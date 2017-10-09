package com.keessi.shiro.guice;

import com.google.inject.Provides;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.name.Names;
import org.apache.shiro.config.Ini;
import org.apache.shiro.guice.web.ShiroWebModule;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;

import javax.inject.Singleton;
import javax.servlet.ServletContext;
import java.net.MalformedURLException;
import java.net.URL;

public class ShiroNativeSessionsServletModule extends ShiroWebModule {
    private final ServletContext servletContext;

    public ShiroNativeSessionsServletModule(ServletContext servletContext) {
        super(servletContext);
        this.servletContext = servletContext;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void configureShiroWeb() {
        bindConstant().annotatedWith(Names.named("shiro.loginUrl")).to("/login.jsp");
        try {
            this.bindRealm().toConstructor(IniRealm.class.getConstructor(Ini.class));
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
    protected void bindSessionManager(AnnotatedBindingBuilder<SessionManager> bind) {
        bind.to(DefaultWebSessionManager.class);
        bindConstant().annotatedWith(Names.named("shiro.globalSessionTimeout")).to(5000L);
        bind(DefaultWebSessionManager.class);
        bind(Cookie.class).toInstance(new SimpleCookie("myCookie"));
    }
}
