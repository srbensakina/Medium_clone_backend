package com.api.medium_clone.dto;


import com.api.medium_clone.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentListResponseDto {

    private List<Comment> comments;

}
