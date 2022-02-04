package org.after90.metadataManager.file;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import java.io.File;
import java.io.FilenameFilter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.springframework.util.DigestUtils;
import org.springframework.util.FileCopyUtils;

@Service
@Slf4j
public class FileService {

  /**
   * copy文件到目标文件。如果存在，且md5一样，跳过；如果重名，写一个新的
   *
   * @param srcFile
   * @param dstFile
   * @throws Exception
   */
  public void copyFile(File srcFile, File dstFile) throws Exception {
    if (!srcFile.exists()) {
      log.warn("srcFile not exist, file: {}", srcFile.getAbsolutePath());
      return;
    }
    var dstPath = new File(dstFile.getParent());
    if (!dstPath.exists()) {
      dstPath.mkdirs();
    }
    // 目标文件存在的情况
    if (dstFile.exists()) {
      if (srcFile.length() == dstFile.length()) {
        log.warn("same file");
        log.info("srcFile: {}", srcFile.getAbsolutePath());
        log.info("dstFile: {}", dstFile.getAbsolutePath());
        return;
      }
      while (true) {
        var dstFileNewStr =
            dstFile.getAbsolutePath() + "_" + ((Double) (Math.random() * 1000)).intValue();
        var dstFileNew = new File(dstFileNewStr);
        if (!dstFileNew.exists()) {
          dstFile = dstFileNew;
          break;
        }
      }
    }
    FileCopyUtils.copy(srcFile, dstFile);
    log.info("copy file, {} to {}", srcFile.getCanonicalPath(), dstFile.getCanonicalPath());
  }

  // 2022:01:15 10:55:09
  private DateTimeFormatter dtfA = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
  // Tue Jan 25 16:35:27 +08:00 2022
  private DateTimeFormatter dtfB = DateTimeFormatter.ofPattern("EEE LLL dd HH:mm:ss zzz yyyy");

  public Optional<ZonedDateTime> getCreateDate(File file) {
    if (file.exists()) {
      var fileNameLowerCase = file.getAbsolutePath().toLowerCase();
      try {
        if (fileNameLowerCase.endsWith(".jpg") || fileNameLowerCase.endsWith(".png")
            || fileNameLowerCase.endsWith(".heic") || fileNameLowerCase.endsWith(".jpeg")
            || fileNameLowerCase.endsWith(".cr2")) {
          Metadata metadata = ImageMetadataReader.readMetadata(file);
          for (Directory directory : metadata.getDirectories()) {
            if ("Exif SubIFD".equals(directory.getName())) {
              var desc = directory.getDescription(36867);
              if (desc != null) {
                LocalDateTime localDateTime = LocalDateTime.parse(desc, dtfA);
                ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime,
                    ZoneId.systemDefault());
                return Optional.of(zonedDateTime);
              }
            }
          }
        } else if (fileNameLowerCase.endsWith(".mov") || fileNameLowerCase.endsWith(".mp4")) {
          Metadata metadata = ImageMetadataReader.readMetadata(file);
          for (Directory directory : metadata.getDirectories()) {
            if ("QuickTime".equals(directory.getName()) || "MP4".equals(directory.getName())) {
              var desc = directory.getDescription(256);
              if (desc != null) {
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(desc,
                    dtfB);
                return Optional.of(zonedDateTime);
              }
            }
          }
        } else {
          log.warn("file endWith not match: {}", file.getAbsolutePath());
        }
      } catch (Exception e) {
        log.error("get create time err, file: {}", file.getAbsolutePath(), e);
      }
    } else {
      log.warn("file not exist, file: {}", file.getAbsolutePath());
    }
    return Optional.empty();
  }


  /**
   * 找同级目录中相同的文件,保留文件名最短的，删除其它
   *
   * @param path
   */
  public void deleteSameFile(File path) {
    log.info("path: {}", path.getAbsolutePath());
    if (!path.exists() && path.isDirectory()) {
      log.warn("path not exists");
      return;
    }
    var listFiles = path.listFiles(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        String[] notStartWithList = {"."};
        for (String startWith : notStartWithList) {
          if (name.toLowerCase().startsWith(startWith)) {
            log.info("ignore file: {}/{}", dir, name);
            return false;
          }
        }
        return true;
      }
    });
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
