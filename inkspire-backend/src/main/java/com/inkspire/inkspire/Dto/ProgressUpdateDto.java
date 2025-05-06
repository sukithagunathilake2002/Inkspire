package com.inkspire.inkspire.Dto;

import com.inkspire.inkspire.model.ProgressTemplate;

import lombok.Data;


@Data

public class ProgressUpdateDto {
     private Long id;
    private String userId;
    private ProgressTemplate progressTemplate;
    private String description;
    private String plan;
}
