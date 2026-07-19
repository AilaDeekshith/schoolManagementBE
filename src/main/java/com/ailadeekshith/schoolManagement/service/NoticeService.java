package com.ailadeekshith.schoolManagement.service;

import com.ailadeekshith.schoolManagement.model.Notice;

import java.util.List;

public interface NoticeService {
    List<Notice> getAll();
    Notice create(Notice notice);
    Notice update(Long id, Notice notice);
    void delete(Long id);
}
