package org.after90.photoManager.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PhotoServiceTest {

  @Autowired
  PhotoService photoService;

  @Test
  void photoManager() {
    photoService.photoManager();
  }
}