package com.marcobehler;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;

public class ApplicationLauncher {

    public static void main(String[] args) throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);

        final Context ctx = tomcat.addContext("", null);
        final Wrapper hello = Tomcat.addServlet(ctx, "hello", new DispatcherServlet());
        hello.addMapping("/*");

        tomcat.getConnector();
        tomcat.start();
    }
}
