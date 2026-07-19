package com.ailadeekshith.schoolManagement.service.impl;

import com.ailadeekshith.schoolManagement.exception.ResourceNotFoundException;
import com.ailadeekshith.schoolManagement.model.Notice;
import com.ailadeekshith.schoolManagement.repository.NoticeRepository;
import com.ailadeekshith.schoolManagement.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepo;

    @Override
    @Transactional(readOnly = true)
    public List<Notice> getAll() {
        return noticeRepo.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public Notice create(Notice notice) {
        notice.setId(null);
        return noticeRepo.save(notice);
    }

    @Override
    public Notice update(Long id, Notice incoming) {
        Notice existing = noticeRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notice not found: " + id));
        existing.setTitle(incoming.getTitle());
        existing.setContent(incoming.getContent());
        existing.setImageBase64(incoming.getImageBase64());
        return noticeRepo.save(existing);
    }

    @Override
    public void delete(Long id) {
        Notice existing = noticeRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notice not found: " + id));
        noticeRepo.delete(existing);
    }
}
