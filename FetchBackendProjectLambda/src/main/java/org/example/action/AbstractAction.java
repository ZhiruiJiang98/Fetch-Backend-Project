package org.example.action;

import org.example.library.model.constant.ActionResponseStatus;
import org.example.library.model.server.ActionResponse;

import java.io.IOException;
import java.sql.SQLException;

public interface AbstractAction <I, O> {
    public String getActionName();
    public ActionResponse<O> processRequest(I event) throws SQLException, IOException, NoSuchFieldException, IllegalAccessException;
    public boolean requestValidated(I event);

    default ActionResponse<O> constructResponse(
            ActionResponseStatus actionResponseStatus,
            String errorMessage,
            O data,
            String actionName
    ) {
        return ActionResponse.<O>builder()
                .actionName(actionName)
                .data(data)
                .errorMessage(errorMessage)
                .actionResponseStatus(actionResponseStatus)
                .build();
    }
}
