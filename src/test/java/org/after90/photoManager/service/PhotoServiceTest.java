package org.after90.photoManager.service;

import java.io.File;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PhotoServiceTest {

  @Autowired
  PhotoService photoService;

  @Test
  @Disabled
  void photoManager() {
    photoService.photoManager(new File("/Volumes/photo/photo/19xx"),
        new File("/Users/zhaoguangjian/tmp/output"));
  }

  @Test
  void findSameFile() {
    photoService.findSameFile(new File("/Volumes/photo/photo"));
  }
}