package org.minjae;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


/**
 * packageName       : org.minjae
 * fileName         : ${NAME}
 * author           : MinjaeKim
 * date             : 2024-08-13
 * description      :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-08-13        MinjaeKim       최초 생성
 */
public class WebApplicationServerLauncher {

    private static final Logger logger = LoggerFactory.getLogger(WebApplicationServerLauncher.class);

    public static void main(String[] args) throws LifecycleException {
        String webappDirLocation ="webapp/";
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);

        tomcat.addWebapp("/", new File(webappDirLocation).getAbsolutePath());
        logger.info("configuring app with basedir : {}", new File( webappDirLocation).getAbsolutePath());

        tomcat.start();
        tomcat.getServer().await();
    }
}