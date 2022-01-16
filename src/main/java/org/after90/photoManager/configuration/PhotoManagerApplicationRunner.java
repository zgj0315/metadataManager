package org.after90.photoManager.configuration;

import lombok.extern.slf4j.Slf4j;
import org.after90.photoManager.utils.ParaUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PhotoManagerApplicationRunner implements ApplicationRunner {

  @Override
  public void run(ApplicationArguments args) throws Exception {
    if (!ParaUtils.TESTING) {
      log.info("Begin ApplicationRunner...");

      log.info("End ApplicationRunner");
    }
  }
}
