package com.huynh.ZaloCloneBe.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageRequest {
    private Long senderId;
    private Long receiverId;
    @NotBlank(message = "CONTENT_REQUIRED")
    private String content;
}
