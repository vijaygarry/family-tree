package com.neasaa.familytree.operation.events.model;

import com.neasaa.base.app.operation.model.OperationResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class GetEventsResponse extends OperationResponse {

    private List<EventDto> events;
    private int totalEvents;
    private int totalPages;
    private int currentPage;
    private int pageSize;
}
