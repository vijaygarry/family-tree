package com.neasaa.familytree.operation.events;

import com.neasaa.base.app.operation.AbstractOperation;
import com.neasaa.base.app.operation.exception.OperationException;
import com.neasaa.base.app.operation.exception.ValidationException;
import com.neasaa.familytree.operation.OperationNames;
import com.neasaa.familytree.operation.events.model.EventDto;
import com.neasaa.familytree.operation.events.model.GetEventsRequest;
import com.neasaa.familytree.operation.events.model.GetEventsResponse;
import com.neasaa.familytree.operation.family.model.AddressDto;
import com.neasaa.familytree.operation.family.model.GetFamilyDetailsRequest;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("GetEventsOperation")
@Scope("prototype")
public class GetEventsOperation extends AbstractOperation<GetEventsRequest, GetEventsResponse> {

    @Override
    public String getOperationName() {
        return OperationNames.GET_EVENTS;
    }

    @Override
    public void doValidate(GetEventsRequest opRequest) throws OperationException {

    }

    @Override
    public GetEventsResponse doExecute(GetEventsRequest opRequest) throws OperationException {
        List<EventDto> events = new ArrayList<>();
        AddressDto address = AddressDto.builder()
                .addressLine1("Address Line1")
                .addressLine2("Address Line2")
                .addressLine3("Address Line3")
                .city("City")
                .state("State")
                .country("Country")
                .postalCode("444444")
                .build();
        EventDto event = EventDto.builder()
                .eventId(1)
                .eventType("Birthday")
                .title("Someone's Birthday")
                .description("Coming Friday is Someone's birthday. Let's celebrate! All are invited.")
                .eventDate("2025-08-01")
                .eventTime("From 6:00 PM to 9:00 PM")
                .eventPlace(address)
                .eventOrganizer("Mr. Sanjayrao Deshmukh")
                .notes("Please bring your own drinks and snacks.")
                .eventImage("https://example.com/event-image.jpg")
                .build();
        events.add(event);
        event = EventDto.builder()
                .eventId(2)
                .eventType("Wedding")
                .title("Someone's Wedding")
                .description("With great pleasure, we invite you to the wedding of Someone and Someone. Please join us in celebrating this joyous occasion. All are invited.")
                .eventDate("2025-08-01")
                .eventTime("From 6:00 PM to 9:00 PM")
                .eventPlace(address)
                .eventOrganizer("Mr. Sanjayrao Deshmukh")
                .notes("Please bring your own drinks and snacks.")
                .eventImage("https://example.com/event-image.jpg")
                .build();
        events.add(event);
        event = EventDto.builder()
                .eventId(3)
                .eventType("Death")
                .title("Someone is not more")
                .description("With great sorrow, we announce the passing of Someone. Please join us in honoring their memory. All are invited.")
                .eventDate("2025-08-01")
                .eventTime("From 6:00 PM to 9:00 PM")
                .eventPlace(address)
                .eventOrganizer("Mr. Sanjayrao Deshmukh")
                .notes("Please bring your own drinks and snacks.")
                .eventImage("https://example.com/event-image.jpg")
                .build();
        events.add(event);
        event = EventDto.builder()
                .eventId(4)
                .eventType("Birthday")
                .title("Someone's Birthday")
                .description("Coming Friday is Someone's birthday. Let's celebrate! All are invited.")
                .eventDate("2025-08-01")
                .eventTime("From 6:00 PM to 9:00 PM")
                .eventPlace(address)
                .eventOrganizer("Mr. Sanjayrao Deshmukh")
                .notes("Please bring your own drinks and snacks.")
                .eventImage("https://example.com/event-image.jpg")
                .build();
        events.add(event);


        return GetEventsResponse.builder()
                .events(events) // Placeholder for actual event data
                .totalEvents(4) // Placeholder for total events count
                .totalPages(3) // Placeholder for total pages
                .currentPage(1) // Default to first page
                .pageSize(4) // Default page size
                .build();
    }
}
