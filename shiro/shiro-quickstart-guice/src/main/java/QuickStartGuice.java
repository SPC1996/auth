import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuickStartGuice {
    private static final transient Logger log = LoggerFactory.getLogger(QuickStartGuice.class);

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new QuickStartShiroModule());
        SecurityManager securityManager = injector.getInstance(SecurityManager.class);

        SecurityUtils.setSecurityManager(securityManager);

        Subject currentUser = SecurityUtils.getSubject();

        Session session = currentUser.getSession();
        session.setAttribute("key", "value");
        String value = (String) session.getAttribute("key");
        if (value.equals("value")) {
            log.info("Retrieved the correct value! [" + value + "]");
        }

        if (!currentUser.isAuthenticated()) {
            UsernamePasswordToken token = new UsernamePasswordToken("root", "secret");
            token.setRememberMe(true);
            try {
                currentUser.login(token);
            } catch (UnknownAccountException uae) {
                log.info("There is no user with username of " + token.getPrincipal());
            } catch (IncorrectCredentialsException ice) {
                log.info("Password for account " + token.getPrincipal() + " was incorrect!");
            } catch (LockedAccountException lae) {
                log.info("The account for username " + token.getPrincipal() + " is locked. "
                        + "Please contact your administrator to unlock it.");
            } catch (AuthenticationException ae) {
                log.info("Unknown Exception");
            }
        }

        log.info("User [" + currentUser.getPrincipal() + "] logged in successfully.");
        if (currentUser.hasRole("admin")) {
            log.info("You are a administrator!");
        } else {
            log.info("You are a ...");
        }

        if (currentUser.isPermitted("manager:all")) {
            log.info("You can manage all parts!");
        } else {
            log.info("Some parts that you can't manage!");
        }

        if (currentUser.isPermitted("manager:user:add")) {
            log.info("You can add a user");
        } else {
            log.info("You can't add a user");
        }

        currentUser.logout();

        System.exit(1);
    }
}
