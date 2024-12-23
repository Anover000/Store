package com.example.store.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponseDTO {

    String error;
    String message;
    LocalDateTime timestamp;
}
