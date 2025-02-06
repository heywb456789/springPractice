package org.minjae;

import java.io.File;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : MinjaeKim
 * @packageName : org.minjae
 * @fileName : ${NAME}
 * @date : 2025-02-05
 * @description : ===========================================================
 * @DATE @AUTHOR       @NOTE ----------------------------------------------------------- 2025-02-05
 * MinjaeKim       최초 생성
 */
public class WebApplicationServer {

    private static final Logger log = LoggerFactory.getLogger(WebApplicationServer.class);

    public static void main(String[] args) throws Exception {
        // main Method 실행시 톰캣이 실행되도록
        String webappDirLocation = "webapps/";

        // webapps 폴더가 존재하지 않으면 생성
        File webappsDir = new File(webappDirLocation);
        if (!webappsDir.exists()) {
            webappsDir.mkdir(); // 폴더 생성
            log.info("webapps 디렉토리가 생성되었습니다: {}", webappsDir.getAbsolutePath());
        }

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        log.info("hi : {}", "/" + webappsDir.getAbsolutePath());

        tomcat.addWebapp("/", webappsDir.getAbsolutePath());
        log.info("configuring app with basedir : {}", new File("./" + webappDirLocation).getAbsolutePath());

        tomcat.start();
        tomcat.getServer().await();

    }
}