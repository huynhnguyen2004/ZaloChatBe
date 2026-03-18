package com.huynh.ZaloCloneBe.service;

import com.huynh.ZaloCloneBe.exception.AppException;
import com.huynh.ZaloCloneBe.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileService {
    private final Path root = Paths.get("uploads/avatars");
    private final Path rootCover = Paths.get("uploads/covers");
    public String uploadAvatar(MultipartFile file,Long userId) throws Exception{
        if(file.isEmpty()){
            throw new AppException(ErrorCode.FILE_EMPTY);
        }
        if(!file.getContentType().startsWith("image/")){
            throw new AppException(ErrorCode.INVALID_IMAGE_FORMAT);
        }
        Path userDir=root.resolve(("user_")+userId);
        Files.createDirectories(userDir);
        String ext=file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.')+1);
        String filename="avatar."+ext;
        Path filePath=userDir.resolve(filename);
        Files.copy(file.getInputStream(),filePath, StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/avatars/user_"+userId+"/"+filename;
    }
    public String uploadCover(MultipartFile file,Long userId) throws Exception{
        if(file.isEmpty()){
            throw new AppException(ErrorCode.FILE_EMPTY);
        }
        if(!file.getContentType().startsWith("image/")){
            throw new AppException(ErrorCode.INVALID_IMAGE_FORMAT);
        }
        Path userDir=rootCover.resolve(("user_")+userId);
        Files.createDirectories(userDir);
        String ext=file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.')+1);
        String filename="avatar."+ext;
        Path filePath=userDir.resolve(filename);
        Files.copy(file.getInputStream(),filePath, StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/covers/user_"+userId+"/"+filename;
    }
}
