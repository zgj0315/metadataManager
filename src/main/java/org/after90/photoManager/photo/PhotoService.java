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


  /**
   * 找同级目录中相同的文件,保留文件名最短的，删除其它
   *
   * @param path
   */
  public void deleteSameFile(File path) {
    if (!path.exists()) {
      log.warn("path not exists");
      return;
    }
    var listFiles = path.listFiles();
    Map<String, List<String>> fileMap = new HashMap();
    // 采用两层结构，优化md5计算慢的问题
    Map<Long, List<File>> sizeMap = new HashMap();
    for (File file : listFiles) {
      if (file.isFile()) {
        var size = file.length();
        if (sizeMap.containsKey(size)) {
          sizeMap.get(size).add(file);
        } else {
          List<File> sameSizeFileList = new ArrayList<>();
          sameSizeFileList.add(file);
          sizeMap.put(size, sameSizeFileList);
        }
      } else if (file.isDirectory()) {
        deleteSameFile(file);
      }
    }
    // 处理文件大小一致的
    for (List<File> sameSizeFileList : sizeMap.values()) {
      if (sameSizeFileList.size() >= 2) {
        Map<String, List<File>> md5Map = new HashMap();
        for (File sameSizeFile : sameSizeFileList) {
          try {
            var md5 = DigestUtils.md5DigestAsHex(FileCopyUtils.copyToByteArray(sameSizeFile));
            if (md5Map.containsKey(md5)) {
              md5Map.get(md5).add(sameSizeFile);
            } else {
              List<File> sameMd5FileList = new ArrayList<>();
              sameMd5FileList.add(sameSizeFile);
              md5Map.put(md5, sameMd5FileList);
            }
          } catch (Exception e) {
            log.error("md5 err", e);
          }
        }
        for (List<File> sameMd5FileList : md5Map.values()) {
          if (sameMd5FileList.size() >= 2) {
            log.info("same file list:");
            int fileSize = Integer.MAX_VALUE;
            File keepFile = null;
            for (File sameMd5File : sameMd5FileList) {
              String fileName = sameMd5File.getName();
              if (fileName.length() < fileSize) {
                fileSize = fileName.length();
                keepFile = sameMd5File;
              }
            }
            for (File sameMd5File : sameMd5FileList) {
              if (keepFile.equals(sameMd5File)) {
                log.info("keep   file: {}", sameMd5File.getAbsolutePath());
              } else {
                log.info("delete file: {}", sameMd5File.getAbsolutePath());
                sameMd5File.delete();
              }
            }
          }
        }
      }
    }
  }
}
