package org.example.library.model.server;

import lombok.Builder;
import lombok.Data;
import org.example.library.model.constant.Status;

@Data
@Builder
public class ApiResponseBody <T> {
    private Status status;
    private T data;
    private String errorMessage;
    private String message;
    private String code;
    private String actionName;
    private String serverTime;
}
