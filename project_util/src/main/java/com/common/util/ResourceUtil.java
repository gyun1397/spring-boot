package com.common.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class ResourceUtil {
    
    public static Resource getResource(String filePath) throws Exception, IOException {
        filePath = StringUtil.nvl(filePath);
        if (filePath.contains("classpath:")) { // spring 에서 src.main.resources 폴더 아래에서 리소스를 찾을때
            return new ClassPathResource(StringUtil.remove(filePath,"classpath:"));
//        } else if (filePath.contains("/common/")) {// common 에서 리소스를 찾을때
//            File file = new File(filePath);
//            return new ByteArrayResource(Files.readAllBytes(file.toPath()));
        } else { 
            // pure java 에서 src.main.resources 폴더 아래에서 리소스를 찾을때
            ClassPathResource classPathResource = new ClassPathResource("static/"+filePath);
            if (classPathResource.exists()) {
                return classPathResource;
            } else {
                // 절대경로에서 찾을 때
                File file = new File(filePath);
                return new ByteArrayResource(Files.readAllBytes(file.toPath()));
            }
        }
    }
    
    public static File getFile(String filePath) throws Exception, IOException {
        filePath = StringUtil.nvl(filePath);
        if (filePath.contains("classpath:")) { // spring 에서 src.main.resources 폴더 아래에서 리소스를 찾을때
            return new ClassPathResource(StringUtil.remove(filePath,"classpath:")).getFile();
//        } else if (filePath.contains("/common/")) {// common 에서 리소스를 찾을때
//            File file = new File(filePath);
//            return new ByteArrayResource(Files.readAllBytes(file.toPath()));
        } else { 
            // pure java 에서 src.main.resources 폴더 아래에서 리소스를 찾을때
            ClassPathResource classPathResource = new ClassPathResource("static/"+filePath);
            if (classPathResource.exists()) {
                return classPathResource.getFile();
            } else {
                // 절대경로에서 찾을 때
                File file = new File(filePath);
                if (file.exists()) {
                    return file;
                }
                return new ByteArrayResource(Files.readAllBytes(file.toPath())).getFile();
            }
        }
        
    }
}
