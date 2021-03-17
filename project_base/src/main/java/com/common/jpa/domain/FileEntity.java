package com.common.jpa.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import org.springframework.web.multipart.MultipartFile;
import com.common.util.FileUtil;
import com.common.util.StringUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@MappedSuperclass
public class FileEntity<ID> extends LogEntity<ID> {
    @Column(name = "FILE_PATH", columnDefinition = "text")
    protected String   filePath;  // file의 상대 경로
    @Column(name = "FILE_NAME")
    protected String    fileName;
    @Column(name = "ORIGIN_FILE_NAME")
    protected String    originalFileName;
    @Column(name = "FILE_EXT")
    protected String    fileExtension;
    @Column(name = "CONTENT_TYPE")
    protected String    contentType;
    @Column(name = "FILE_SIZE", columnDefinition = "bigint")
    protected Long    fileSize;
    @Column(name = "UID")
    protected String    uid;
    
    public FileEntity(String filePath, String fileName) {
        this(filePath,fileName,null,null,null,null,null);
    }
    
    public FileEntity(String filePath, String fileName, String originalFileName, String fileExtension, String contentType, Long fileSize, String uid) {
        this.filePath = filePath;
        this.fileName = fileName;
        this.originalFileName = originalFileName;
        this.fileExtension = fileExtension;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.uid = uid;
    }
    
    public FileEntity(FileEntity<ID> fileEntity) {
        this(fileEntity.getFilePath(), fileEntity.getFileName(), fileEntity.getOriginalFileName(),
                fileEntity.getFileExtension(), fileEntity.getContentType(), 
                fileEntity.getFileSize(), fileEntity.getUid());
    }
    
    public FileEntity(MultipartFile multipartFile) {
        this("", multipartFile.getOriginalFilename(), multipartFile.getOriginalFilename(),
                FileUtil.getExtension(multipartFile.getOriginalFilename()) , multipartFile.getContentType(),
                multipartFile.getSize(), StringUtil.getUUID());
    }
}
