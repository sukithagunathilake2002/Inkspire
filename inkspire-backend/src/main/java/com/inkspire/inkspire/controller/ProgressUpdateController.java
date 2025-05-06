package com.inkspire.inkspire.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.inkspire.inkspire.Dto.ProgressUpdateDto;
import com.inkspire.inkspire.service.ProgressUpdateService;

import java.util.List;

@RestController
@RequestMapping("/api/progress-updates")
public class ProgressUpdateController {

    private final ProgressUpdateService progressUpdateService;

    @Autowired
    public ProgressUpdateController(ProgressUpdateService progressUpdateService) {
        this.progressUpdateService = progressUpdateService;
    }

    @PostMapping
    public ProgressUpdateDto create(@RequestBody ProgressUpdateDto dto) {
        return progressUpdateService.createProgressUpdate(dto);
    }

    @GetMapping
    public List<ProgressUpdateDto> getAll() {
        return progressUpdateService.getAllProgressUpdates();
    }

    @GetMapping("/user/{userId}")
    public List<ProgressUpdateDto> getByUserId(@PathVariable String userId) {
        return progressUpdateService.getProgressUpdatesByUserId(userId);
    }

    @GetMapping("/{id}")
    public ProgressUpdateDto getById(@PathVariable Long id) {
        return progressUpdateService.getProgressUpdateById(id);
    }

    @PutMapping("/{id}")
    public ProgressUpdateDto update(@PathVariable Long id, @RequestBody ProgressUpdateDto dto) {
        return progressUpdateService.updateProgressUpdate(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        progressUpdateService.deleteProgressUpdate(id);
    }
}

