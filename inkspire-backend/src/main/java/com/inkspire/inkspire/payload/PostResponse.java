package com.inkspire.inkspire.payload;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostResponse {
    private Long id;
    private String description;
    private List<String> mediaUrls;
    private String creatorName;
    private boolean isPrivate;
    private boolean isVideo;
    private LocalDateTime createdAt;
}
