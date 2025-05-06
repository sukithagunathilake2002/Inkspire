package com.inkspire.inkspire.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inkspire.inkspire.model.ProgressUpdate;


public interface ProgressUpdateRepository extends JpaRepository<ProgressUpdate, Long> {
    List<ProgressUpdate> findByUserId(String userId);
}