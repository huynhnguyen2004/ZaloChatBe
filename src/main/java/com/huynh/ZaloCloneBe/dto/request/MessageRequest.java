package com.huynh.ZaloCloneBe.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageRequest {
    @NotNull
    private Long conversationId;
    @NotBlank(message = "CONTENT_REQUIRED")
    private String content;
}
