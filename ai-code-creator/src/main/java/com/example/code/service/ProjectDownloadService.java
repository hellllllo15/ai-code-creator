package com.example.code.service;


import jakarta.servlet.http.HttpServletResponse;

///文件下载功能
public interface ProjectDownloadService {





    /// 下载压缩包
    public void downloadProjectAsZip(String projectPath, String downloadFileName, HttpServletResponse response);
}
