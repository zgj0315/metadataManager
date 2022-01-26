package org.after90.photoManager.photo;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.after90.photoManager.file.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.FileCopyUtils;

@Service
@Slf4j
public class PhotoService {

  @Autowired
  private FileService fileService;

  /**
   * 按照图片的创建时间，分类存储
   *
   * @param srcPath  扫描目录
   * @param endWiths 文件后缀，逗号分隔
   * @param dstPath  输出目录
   */
  public void photoManager(File srcPath, String endWiths, File dstPath) {
    if (!srcPath.exists()) {
      log.warn("srcPath not exists");
      return;
    }
    try {
      if (srcPath.getCanonicalPath().equalsIgnoreCase(dstPath.getCanonicalPath())) {
        log.info("srcPath: {}", srcPath.getCanonicalPath());
        log.info("dstPath: {}", dstPath.getCanonicalPath());
        log.warn("srcPath is same with dstPath");
        return;
      }
    } catch (Exception e) {
      log.error("path err", e);
    }
    var endWithList = endWiths.split(",");
    var listFiles = srcPath.listFiles(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        boolean isAccept = new File(dir, name).isDirectory();
        for (String endWith : endWithList) {
          if (isAccept) {
            break;
          }
          isAccept = isAccept || name.toLowerCase().endsWith(endWith);
        }
        if (!isAccept) {
          log.info("ignore file: {}", dir + "/" + name);
        }
        return isAccept;
      }
    });
    for (File file : listFiles) {
      if (file.isFile()) {
        try {
          var zonedDateTimeOptional = fileService.getCreateDate(file);
          if (zonedDateTimeOptional.isPresent()) {
            String year = String.valueOf(zonedDateTimeOptional.get().getYear());
            String month = String.valueOf(zonedDateTimeOptional.get().getMonthValue());
            if (month.length() == 1) {
              month = "0" + month;
            }
            String day = String.valueOf(zonedDateTimeOptional.get().getDayOfMonth());
            if (day.length() == 1) {
              day = "0" + day;
            }
            File dstFile = new File(
                dstPath.getPath() + File.separator + year
                    + File.separator
                    + year + month + File.separator + year
                    + month
                    + day + File.separator + file.getName());
            fileService.copyFile(file, dstFile);
          } else {
            log.info("can not read create time: {}", file.getAbsolutePath());
          }
        } catch (Exception e) {
          log.error("get create time err, file: {}", file.getAbsolutePath(), e);
        }
      } else if (file.isDirectory()) {
        photoManager(file, endWiths, dstPath);
      }
    }
  }
}
