package com.neasaa.familytree.operation.events.model;

import com.neasaa.base.app.operation.model.OperationRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetEventsRequest extends OperationRequest {
    private String eventType;
    private boolean pastEvents;
}
