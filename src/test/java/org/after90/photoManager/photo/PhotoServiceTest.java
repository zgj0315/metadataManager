package org.after90.photoManager.photo;

import java.io.File;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class PhotoServiceTest {

  @Autowired
  PhotoService photoService;

  @Test
  @Disabled
  void photoManager() throws Exception {
    // photoService.photoManager(new File("/Users/zhaoguangjian/tmp/input"), ".jpg,.jpeg",
    //    new File("/Users/zhaoguangjian/tmp/output"));
    // photoService.photoManager(new File("/Users/zhaoguangjian/tmp/metaData"), ".jpg,.jpeg",
    //    new File("/Users/zhaoguangjian/tmp/output"));
    // 整理RAW文件
    // photoService.photoManager(new File("/Volumes/photo/photo"), ".cr2",
    //    new File("/Volumes/photo/original"));
    // photoService.photoManager(new File("/Volumes/home/Photos/Moments/Mobile"), ".cr2",
    //    new File("/Volumes/photo/original"));

    // 整理图片文件
    // photoService.photoManager(new File("/Volumes/photo/photo"), ".jpg,jpeg",
    //    new File("/Volumes/photo/export"));
    // photoService.photoManager(new File("/Volumes/home/Photos/Moments/Mobile"), ".jpg,jpeg",
    //    new File("/Volumes/photo/export"));

    // 整理视频文件
    // photoService.photoManager(new File("/Volumes/photo/photo"), ".mp4,mov",
    //    new File("/Volumes/video/export"));
    // photoService.photoManager(new File("/Volumes/home/Photos/Moments/Mobile"), ".mp4,mov",
    //    new File("/Volumes/video/export"));
  }

  @Test
  @Disabled
  void deleteSameFile() {
    // photoService.deleteSameFile(new File("/Volumes/photo/original"));
    // photoService.deleteSameFile(new File("/Volumes/photo/export"));
    // photoService.deleteSameFile(new File("/Volumes/video/export"));
  }
}