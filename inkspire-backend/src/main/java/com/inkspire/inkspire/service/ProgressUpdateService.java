package com.inkspire.inkspire.service;

import java.util.List;

import com.inkspire.inkspire.Dto.ProgressUpdateDto;



public interface ProgressUpdateService {

    ProgressUpdateDto createProgressUpdate(ProgressUpdateDto progressUpdateDto);

    List<ProgressUpdateDto> getAllProgressUpdates();

    List<ProgressUpdateDto> getProgressUpdatesByUserId(String userId);

    ProgressUpdateDto getProgressUpdateById(Long id);

    ProgressUpdateDto updateProgressUpdate(Long id, ProgressUpdateDto progressUpdateDto);

    void deleteProgressUpdate(Long id);

    
}
