package org.after90.photoManager.photo;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.FileCopyUtils;

@Service
@Slf4j
public class PhotoService {

  private SimpleDateFormat formatYear = new SimpleDateFormat("yyyy");
  private SimpleDateFormat formatYearMMdd = new SimpleDateFormat("yyyy-MM-dd");
  private Map<String, String> photoMap = new HashMap();

  /**
   * 按照图片的创建时间，分类存储
   *
   * @param srcPath 要处理的图片目录
   * @param dstPath 分类存储的目标目录
   */
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
        var isCr2 = name.toLowerCase().endsWith(".cr2");
        var isAccept = isDir || isCr2;
        if (!isAccept) {
          log.info("ignore file: {}", dir + "/" + name);
        }
        return isAccept;
      }
    });
    for (File file : listFiles) {
      if (file.isFile()) {
        try {
          var fileName = file.getCanonicalPath();
          // log.warn("fileName: {}", fileName);
          Metadata metadata = ImageMetadataReader.readMetadata(file);
          for (Directory directory : metadata.getDirectories()) {
            for (Tag tag : directory.getTags()) {
              if ("Date/Time Original".equals(tag.getTagName())) {
                var date = convertDate(tag.getDescription());
                File dstFile = new File(
                    dstPath.getPath() + File.separator + date.getYear() + File.separator
                        + date.getYear() + date.getMonth() + File.separator + date.getYear()
                        + date.getMonth()
                        + date.getDay() + File.separator + file.getName());
                copyFile(file, dstFile);
              }
            }
          }
        } catch (Exception e) {
          log.error("ImageMetadataReader err", e);
        }
      } else if (file.isDirectory()) {
        photoManager(file, dstPath);
      }
    }
  }


  private DateDTO convertDate(String desc) throws Exception {
    // 2013:01:20 16:30:04
    var dateList = ((desc.split(" "))[0]).split(":");
    return new DateDTO(dateList[0], dateList[1], dateList[2]);
  }

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
      log.info("mkdirs: {}", dstPath.getCanonicalPath());
    }
    // 目标文件存在的情况
    if (dstFile.exists()) {
      var srcMd5 = DigestUtils.md5DigestAsHex(FileCopyUtils.copyToByteArray(srcFile));
      var dstMd5 = DigestUtils.md5DigestAsHex(FileCopyUtils.copyToByteArray(dstFile));
      if (srcMd5.equals(dstMd5) && srcFile.length() == dstFile.length()) {
        log.warn("srcFile is same with dstFile, srcFile: {}", srcFile.getAbsolutePath());
        return;
      } else {
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
    }
    FileCopyUtils.copy(srcFile, dstFile);
    dstFile.setLastModified(srcFile.lastModified());
    log.info("copy {} to {}", srcFile.getCanonicalPath(), dstFile.getCanonicalPath());
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
