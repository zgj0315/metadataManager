# 媒体文件管理工具
## 简介
1. 按照图片、视频的拍摄时间，按照年月日进行存储
2. 去除重复的文件
3. 支持cr2，jpg，jpeg，jpg，png，heic，mov，mp4

## 使用方法

将/home/zhaogj/tmp目录下的.mp4结尾文件，按照拍摄时间拷贝的/home/zhaogj/video目录，不删除原文件
```java
java -jar metadataManager-1.1.jar copy /home/zhaogj/tmp .mp4 /home/zhaogj/video
```

将/home/zhaogj/tmp目录下的.mp4结尾文件，按照拍摄时间移动的/home/zhaogj/video目录，删除源文件
```java
java -jar metadataManager-1.1.jar move /home/zhaogj/tmp .mp4 /home/zhaogj/video
```

删除/home/zhaogj/video目录下的重复文件，保留文件名最短的文件
```java
java -jar metadataManager-1.1.jar deduplicate /home/zhaogj/video
```
