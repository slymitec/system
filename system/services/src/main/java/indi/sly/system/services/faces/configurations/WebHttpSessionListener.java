package indi.sly.system.services.faces.configurations;

import indi.sly.system.kernel.core.enviroment.values.UserSpaceDefinition;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

@WebListener
public class WebHttpSessionListener implements HttpSessionListener {
    public WebHttpSessionListener() {
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        HttpSession session = se.getSession();

        UserSpaceDefinition userSpace = new UserSpaceDefinition();

        session.setAttribute("userSpace", userSpace);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();

        session.removeAttribute("userSpace");
    }
}
