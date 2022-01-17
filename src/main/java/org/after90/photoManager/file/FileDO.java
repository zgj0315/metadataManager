package org.after90.photoManager.file;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Data
@Table(name = "tbl_file", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"path", "md5", "length"})})
public class FileDO {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String path;
  private String md5;
  private Long length;

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private Date createdDate;

  @UpdateTimestamp
  @Column(nullable = false)
  private Date lastModifiedDate;
}
