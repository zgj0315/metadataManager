package org.after90.photoManager.file;

import java.io.File;
import java.io.FilenameFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.util.DigestUtils;
import org.springframework.util.FileCopyUtils;

@Service
@Slf4j
public class FileService {

  @Autowired
  private FileRepository fileRepository;

  public void findDuplicateFile(File pathScan) {
    var listFiles = pathScan.listFiles(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        var isDir = new File(dir, name).isDirectory();
        var isFile = new File(dir, name).isFile();
        var isHide = name.toLowerCase().startsWith(".");
        var isAccept = (isDir || isFile) && !isHide;
        if (!isAccept) {
          log.info("ignore file: {}", dir + "/" + name);
        }
        return isAccept;
      }
    });
    for (File file : listFiles) {
      if (file.isDirectory()) {
        findDuplicateFile(file);
      }
      if (file.isFile()) {
        var length = file.length();
        if (length > 0) {
          try {
            var md5 = DigestUtils.md5DigestAsHex(FileCopyUtils.copyToByteArray(file));
            var path = file.getAbsolutePath();
            var fileDOList = fileRepository.findByMd5(md5);
            if (!fileDOList.isEmpty()) {
              log.warn("duplicate file: {}", path);
              fileDOList.forEach(_file -> {
                log.warn("duplicate file: {}", _file.getPath());
              });
              log.info("");
            }
            FileDO fileDO = new FileDO();
            fileDO.setPath(path);
            fileDO.setMd5(md5);
            fileDO.setLength(length);
            fileRepository.save(fileDO);
          } catch (Exception e) {
            log.error("read file err", e);
          }
        }
      }
    }
  }
}
