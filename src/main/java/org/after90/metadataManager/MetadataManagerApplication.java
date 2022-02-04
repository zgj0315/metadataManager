package org.after90.metadataManager;

import lombok.extern.slf4j.Slf4j;
import org.after90.metadataManager.utils.ParaUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;

@SpringBootApplication
@Slf4j
public class MetadataManagerApplication {

  public static void main(String[] args) {
    log.info("OS name: {}, arch: {}, version: {}", System.getProperty("os.name"),
        System.getProperty("os.arch"), System.getProperty("os.version"));
    log.info("Java version: {}, vendor: {}, home: {}", System.getProperty("java.version"),
        System.getProperty("java.vendor"), System.getProperty("java.home"));
    log.info("Java vm version: {}, vendor: {}, name: {}", System.getProperty("java.vm.version"),
        System.getProperty("java.vm.vendor"), System.getProperty("java.vm.name"));
    if (args.length == 4 || args.length == 2) {
      // run
    } else {
      log.info("\n\n\nargs illegal, input like this:"
          + "\neg: java -jar metadataManager-1.1.jar copy /Users/zhaoguangjian/tmp/input .jpg,.jpeg /Users/zhaoguangjian/tmp/output"
          + "\neg: java -jar metadataManager-1.1.jar move /Users/zhaoguangjian/tmp/input .jpg,.jpeg /Users/zhaoguangjian/tmp/output"
          + "\neg: java -jar metadataManager-1.1.jar deduplicate /Users/zhaoguangjian/tmp/output"
          + "\n\n\n");
      return;
    }
    ParaUtils.TESTING = false;
    SpringApplication springApplication = new SpringApplication(MetadataManagerApplication.class);
    springApplication.addListeners(new ApplicationPidFileWriter("metadataManagerApplication.pid"));
    springApplication.run(args);
  }

}
