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
    File srcFile = new File("/Users/zhaoguangjian/tmp/copyFile/src/abc.txt");
    File dstFile = new File("/Users/zhaoguangjian/tmp/copyFile/dst/abc.txt");
    dstFile.delete();
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
}