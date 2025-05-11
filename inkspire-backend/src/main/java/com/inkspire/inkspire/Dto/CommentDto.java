package com.inkspire.inkspire.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class CommentDto {
    private String content;
    private String commentBy;
    private String commentById;
    private String commentByProfile;

}
