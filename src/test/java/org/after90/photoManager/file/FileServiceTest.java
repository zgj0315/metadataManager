package org.after90.photoManager.file;

import java.io.File;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class FileServiceTest {

  @Autowired
  FileService fileService;

  @Test
  @Disabled
  void copyFile() throws Exception {
    File srcFile = new File("/Volumes/photo/photo/2012/20120519香山骑行/IMG_2535.CR2");
    File dstFile = new File("/Volumes/photo/original/2012/201205/20120519/IMG_2535.CR2");
    fileService.copyFile(srcFile, dstFile);
  }


  @Test
  @Disabled
  void getCreateDate() throws Exception {
    File path = new File("/Users/zhaoguangjian/tmp/input/2022-01/");
    for (File file : path.listFiles()) {
      log.info("begin file: {}", file.getAbsolutePath());
      if (file.isFile()) {
        try {
          var zonedDateTimeOptional = fileService.getCreateDate(file);
          if (zonedDateTimeOptional.isPresent()) {
            log.info("file: {}, create time: {}", file.getAbsolutePath(),
                zonedDateTimeOptional.get());
          } else {
            log.info("not read create time");
          }
        } catch (Exception e) {
          log.error("get create time", e);
        }
      }
      log.info("end file: {}", file.getAbsolutePath());
    }
    log.info("finish");
  }

  @Test
  @Disabled
  void deleteSameFile() {
    // fileService.deleteSameFile(new File("/Volumes/porn"));
    // fileService.deleteSameFile(new File("/Volumes/photo/original"));
    // fileService.deleteSameFile(new File("/Volumes/photo/export"));
    // fileService.deleteSameFile(new File("/Volumes/video/export"));
  }
}