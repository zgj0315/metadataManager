package org.after90.photoManager.file;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends CrudRepository<FileDO, Long> {

  List<FileDO> findByPathAndMd5(String path, String md5);

  List<FileDO> findByMd5(String md5);
}
