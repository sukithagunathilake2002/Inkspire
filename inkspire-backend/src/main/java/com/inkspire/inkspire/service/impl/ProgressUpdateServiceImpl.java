package com.inkspire.inkspire.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inkspire.inkspire.Dto.ProgressUpdateDto;
import com.inkspire.inkspire.model.ProgressUpdate;
import com.inkspire.inkspire.repository.ProgressUpdateRepository;
import com.inkspire.inkspire.service.ProgressUpdateService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProgressUpdateServiceImpl implements ProgressUpdateService {

    private final ProgressUpdateRepository progressUpdateRepository;

    @Autowired
    public ProgressUpdateServiceImpl(ProgressUpdateRepository progressUpdateRepository) {
        this.progressUpdateRepository = progressUpdateRepository;
    }

    @Override
    public ProgressUpdateDto createProgressUpdate(ProgressUpdateDto dto) {
        ProgressUpdate update = mapToEntity(dto);
        ProgressUpdate saved = progressUpdateRepository.save(update);
        return mapToDto(saved);
    }

    @Override
    public List<ProgressUpdateDto> getAllProgressUpdates() {
        return progressUpdateRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProgressUpdateDto> getProgressUpdatesByUserId(String userId) {
        return progressUpdateRepository.findByUserId(userId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProgressUpdateDto getProgressUpdateById(Long id) {
        ProgressUpdate update = progressUpdateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Progress Update not found"));
        return mapToDto(update);
    }

    @Override
    public ProgressUpdateDto updateProgressUpdate(Long id, ProgressUpdateDto dto) {
        ProgressUpdate update = progressUpdateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Progress Update not found"));

        update.setProgressTemplate(dto.getProgressTemplate());
        update.setDescription(dto.getDescription());
        update.setPlan(dto.getPlan());

        ProgressUpdate updated = progressUpdateRepository.save(update);
        return mapToDto(updated);
    }

    @Override
    public void deleteProgressUpdate(Long id) {
        progressUpdateRepository.deleteById(id);
    }

    private ProgressUpdate mapToEntity(ProgressUpdateDto dto) {
        ProgressUpdate update = new ProgressUpdate();
        update.setId(dto.getId());
        update.setUserId(dto.getUserId());
        update.setProgressTemplate(dto.getProgressTemplate());
        update.setDescription(dto.getDescription());
        update.setPlan(dto.getPlan());
        return update;
    }

    private ProgressUpdateDto mapToDto(ProgressUpdate update) {
        ProgressUpdateDto dto = new ProgressUpdateDto();
        dto.setId(update.getId());
        dto.setUserId(update.getUserId());
        dto.setProgressTemplate(update.getProgressTemplate());
        dto.setDescription(update.getDescription());
        dto.setPlan(update.getPlan());
        return dto;
    }
}
