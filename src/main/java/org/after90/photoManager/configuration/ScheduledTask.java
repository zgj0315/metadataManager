package org.after90.photoManager.configuration;

import lombok.extern.slf4j.Slf4j;
import org.after90.photoManager.utils.ParaUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ScheduledTask {

  @Scheduled(fixedDelay = 1000 * 60 * 1)
  public void sendHostAndVmToReceiver() {
    if (!ParaUtils.TESTING) {
      log.info("Begin ScheduledTask...");

      log.info("End ScheduledTask");
    }
  }
}
