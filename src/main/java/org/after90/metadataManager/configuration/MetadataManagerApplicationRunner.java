package org.after90.metadataManager.configuration;

import java.io.File;
import lombok.extern.slf4j.Slf4j;
import org.after90.metadataManager.file.FileService;
import org.after90.metadataManager.photo.PhotoService;
import org.after90.metadataManager.utils.ParaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MetadataManagerApplicationRunner implements ApplicationRunner {

  @Autowired
  private PhotoService photoService;
  @Autowired
  private FileService fileService;

  @Override
  public void run(ApplicationArguments args) throws Exception {

    if (!ParaUtils.TESTING) {
      if (args.getSourceArgs().length == 4) {
        if ("copy".equals(args.getSourceArgs()[0])) {
          log.info("copy file from {} to {}", args.getSourceArgs()[1], args.getSourceArgs()[3]);
          photoService.photoManager(new File(args.getSourceArgs()[1]), args.getSourceArgs()[2],
              new File(args.getSourceArgs()[3]), false);
        } else if ("move".equals(args.getSourceArgs()[0])) {
          log.info("copy file from {} to {}", args.getSourceArgs()[1], args.getSourceArgs()[3]);
          photoService.photoManager(new File(args.getSourceArgs()[1]), args.getSourceArgs()[2],
              new File(args.getSourceArgs()[3]), true);
        } else {
          log.info("arg illegal: {}", args.getSourceArgs()[0]);
        }
      }
      if (args.getSourceArgs().length == 2) {
        if ("deduplicate".equals(args.getSourceArgs()[0])) {
          log.info("delete same file in {}", args.getSourceArgs()[1]);
          fileService.deleteSameFile(new File(args.getSourceArgs()[1]));
        } else {
          log.info("arg illegal: {}", args.getSourceArgs()[0]);
        }
      }
    }
  }
}
