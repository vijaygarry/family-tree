package com.neasaa.familytree.operation.events.model;

import com.neasaa.familytree.operation.family.model.AddressDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {

    private int eventId;
    private String eventType;
    private String title;
    private String description;
    private AddressDto eventPlace;
    private String eventDate;
    private String eventTime;
    private String eventOrganizer;
    private String notes;
    private String eventImage;
    private List<String> eventPhotos;

}
