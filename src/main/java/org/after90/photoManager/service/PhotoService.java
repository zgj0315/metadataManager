package org.after90.photoManager.service;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;

@Service
@Slf4j
public class PhotoService {

  private SimpleDateFormat formatYear = new SimpleDateFormat("yyyy");
  private SimpleDateFormat formatYearMMdd = new SimpleDateFormat("yyyy-MM-dd");
  private Map<String, String> photoMap = new HashMap();

  public void photoManager(File srcPath, File dstPath) {
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
    var listFiles = srcPath.listFiles(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        var isDir = new File(dir, name).isDirectory();
        var isJpeg = name.toLowerCase().endsWith(".jpeg");
        var isJpg = name.toLowerCase().endsWith(".jpg");
        var isAccept = isDir || isJpeg || isJpg;
        if (!isAccept) {
          log.info("ignore file: {}", dir + "/" + name);
        }
        return isAccept;
      }
    });
    for (File file : listFiles) {
      if (file.isFile()) {
        try {
          copyPhoto(file, dstPath);
        } catch (Exception e) {
          log.error("copy err", e);
        }
      }
      if (file.isDirectory()) {
        photoManager(file, dstPath);
      }
    }
  }

  private void copyPhoto(File photo, File dstPath) throws Exception {
    var md5 = DigestUtils.md5DigestAsHex(FileCopyUtils.copyToByteArray(photo));
    if (photoMap.containsKey(md5)) {
      log.warn("has file: {}, md5: {}", photoMap.get(md5), md5);
    } else {
      photoMap.put(md5, photo.getCanonicalPath());
    }
    if (!dstPath.exists()) {
      dstPath.mkdirs();
      log.info("mkdirs: {}", dstPath.getCanonicalPath());
    }
    var lastModified = new Date(photo.lastModified());
    var year = formatYear.format(lastModified);
    var yearMMdd = formatYearMMdd.format(lastModified);
    var dstPathYearMMdd = new File(dstPath.getCanonicalPath() + "/" + year + "/" + yearMMdd);
    if (!dstPathYearMMdd.exists()) {
      dstPathYearMMdd.mkdirs();
      log.info("mkdirs: {}", dstPathYearMMdd.getCanonicalPath());
    }
    var dstFile = new File(dstPathYearMMdd.getCanonicalPath() + "/" + photo.getName());
    if (dstFile.exists() && dstFile.isFile()) {
      if (photo.lastModified() == dstFile.lastModified()) {
        // 文件相同，跳过
        log.info("ignore same lastModified file: {}", photo.getCanonicalPath());
        return;
      }
    }
    FileCopyUtils.copy(photo, dstFile);
    dstFile.setLastModified(photo.lastModified());
    log.info("copy {} to {}", photo.getCanonicalPath(), dstFile.getCanonicalPath());
  }

  private Map<String, List<String>> fileMap = new HashMap();

  public void findSameFile(File path) {
    if (!path.exists()) {
      log.warn("path not exists");
      return;
    }
    var listFiles = path.listFiles(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        var isDir = new File(dir, name).isDirectory();
        var isJpeg = name.toLowerCase().endsWith(".jpeg");
        var isJpg = name.toLowerCase().endsWith(".jpg");
        var isAccept = isDir || isJpeg || isJpg;
        if (!isAccept) {
          log.info("ignore file: {}", dir + "/" + name);
        }
        return isAccept;
      }
    });
    for (File file : listFiles) {
      if (file.isFile()) {
        try {
          var md5 = DigestUtils.md5DigestAsHex(FileCopyUtils.copyToByteArray(file));
          if (fileMap.containsKey(md5)) {
            fileMap.get(md5).add(file.getCanonicalPath());
            log.info("same md5: {}", md5);
            for (String filePath : fileMap.get(md5)) {
              log.warn("file: {}", filePath);
            }
          } else {
            List list = new ArrayList();
            list.add(file.getCanonicalPath());
            fileMap.put(md5, list);
          }
        } catch (Exception e) {
          log.error("file err", e);
        }
      }
      if (file.isDirectory()) {
        findSameFile(file);
      }
    }
  }
}
