package org.after90.photoManager.file;

import java.io.File;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FileServiceTest {

  @Autowired
  FileService fileService;

  @Test
  @Disabled
  void findDuplicateFile() {
    fileService.findDuplicateFile(new File("/Users/zhaoguangjian/SynologyDrive/cyberkl"));
  }
}