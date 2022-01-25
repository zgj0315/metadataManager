package org.after90.photoManager.photo;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;
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
  void photoManager() {
    // 整理RAW
    // photoService.photoManager(new File("/Volumes/photo/photo"), ".cr2",
    //    new File("/Volumes/photo/original"));
    // 整理JPG
    // photoService.photoManager(new File("/Volumes/photo/photo"), ".jpg",
    //    new File("/Volumes/photo/export"));
    // photoService.photoManager(new File("/Volumes/home/Photos/Moments/Mobile"), ".jpg",
    //    new File("/Volumes/photo/export"));
    photoService.photoManager(new File("/Volumes/home/Photos/Moments/Mobile"), ".mp4",
        new File("/Volumes/video/export"));
    photoService.photoManager(new File("/Volumes/home/Photos/Moments/Mobile"), ".mov",
        new File("/Volumes/video/export"));
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

  @Test
  @Disabled
  void getCreateDate() throws Exception {
    for (File file : (new File("/Users/zhaoguangjian/tmp/metaData")).listFiles()) {
      if (file.isFile()) {
        var dateDTOOptional = photoService.getCreateDate(file);
        if (dateDTOOptional.isPresent()) {
          log.warn("file: {} create date: {}-{}-{}", file.getAbsolutePath(),
              dateDTOOptional.get().getYear(),
              dateDTOOptional.get().getMonthValue(), dateDTOOptional.get().getDayOfMonth());
        } else {
          log.warn("not get date, file, {}", file.getAbsolutePath());
        }
      }
    }
//    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEE LLL dd HH:mm:ss zzz yyyy");
//    ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.systemDefault());
//    log.info("zonedDateTime: {}", zonedDateTime);
//    log.info("zonedDateTimeFormat: {}", zonedDateTime.format(dtf));
//
//    String dateTimeStr = "Thu Jan 20 09:24:55 +08:00 2022";
//    log.info("dataTimeStr: {}", dateTimeStr);
//
//    zonedDateTime = ZonedDateTime.parse(dateTimeStr, dtf);
//    log.info("zonedDateTime: {}", zonedDateTime);
//    log.info("{}-{}-{}", zonedDateTime.getYear(), zonedDateTime.getMonthValue(),
//        zonedDateTime.getDayOfMonth());
//
//    dateTimeStr = "Sun Sep 03 15:01:50 CST 2017";
//    dtf = DateTimeFormatter.ofPattern("EEE LLL dd HH:mm:ss zzz yyyy");
//    zonedDateTime = ZonedDateTime.parse(dateTimeStr, dtf);
//    log.info("zonedDateTime: {}", zonedDateTime);
//    log.info("{}-{}-{}", zonedDateTime.getYear(), zonedDateTime.getMonthValue(),
//        zonedDateTime.getDayOfMonth());
//
//    localDateTime = LocalDateTime.parse(dateTimeStr, dtf);
//    log.info("localDateTime: {}", localDateTime);
//    log.info("{}-{}-{}", localDateTime.getYear(), localDateTime.getMonthValue(),
//        localDateTime.getDayOfMonth());
  }
}