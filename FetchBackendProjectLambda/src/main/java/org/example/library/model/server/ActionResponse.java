package org.example.library.model.server;

import lombok.Builder;
import lombok.Data;
import org.example.library.model.constant.ActionResponseStatus;
import org.example.library.model.constant.Status;

@Data
@Builder
public class ActionResponse <T>{
    private ActionResponseStatus actionResponseStatus;
    private Status getResponseStatus;
    private String message;
    private String errorMessage;
    private T data;
    private String actionName;
    private String serverTime;
    private String code;
}
