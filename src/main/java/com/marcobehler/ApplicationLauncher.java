package com.marcobehler;

import com.marcobehler.keycloak.KeycloakServlet;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;

import javax.servlet.http.HttpServlet;

public class ApplicationLauncher {

    public static void main(String[] args) throws LifecycleException {
        launchTomcat(8082, new KeycloakServlet());
    }

    public static Tomcat launchTomcat(Integer port, HttpServlet httpServlet) throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.getConnector();

        Context context = tomcat.addContext("", null);
        Wrapper servletWrapper = Tomcat.addServlet(context, httpServlet.getClass().getName(), httpServlet);
        servletWrapper.addMapping("/");

        tomcat.start();
        return tomcat;
    }
}
