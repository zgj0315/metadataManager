package org.after90.photoManager.photo;

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
    // 整理RAW
    // photoService.photoManager(new File("/Volumes/photo/photo"), ".cr2",
    //    new File("/Volumes/photo/original"));
    // 整理JPG
    // photoService.photoManager(new File("/Volumes/photo/photo"), ".jpg",
    //    new File("/Volumes/photo/export"));
    photoService.photoManager(new File("/Volumes/homes"), ".jpg",
        new File("/Volumes/photo/export"));

  }

  @Test
  @Disabled
  void findSameFile() {
    photoService.findSameFile(new File("/Volumes/photo/photo"));
  }

  @Test
  @Disabled
  void copyFile() throws Exception {
    File srcFile = new File("/Users/zhaoguangjian/tmp/copyFile/src/abc.txt");
    File dstFile = new File("/Users/zhaoguangjian/tmp/copyFile/dst/abc.txt");

    photoService.copyFile(srcFile, dstFile);
  }
}