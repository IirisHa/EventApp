package com.example.application.views.events;

import com.example.application.data.entities.Event;
import com.example.application.data.entities.Instrument;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Background;
import com.vaadin.flow.theme.lumo.LumoUtility.BorderRadius;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.FontWeight;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Overflow;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import com.vaadin.flow.theme.lumo.LumoUtility.Width;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class EventsViewCard extends ListItem {

    // Define some image URLs that we can use for different events
    private static final String[] IMAGE_URLS = {
            "https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=750&q=80",
            "https://images.unsplash.com/photo-1533174072545-7a4b6ad7a6a3?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=750&q=80",
            "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=750&q=80",
            "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=750&q=80",
            "https://images.unsplash.com/photo-1501386761578-eac5c94b800a?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=750&q=80"
    };

    public EventsViewCard(Event event) {
        addClassNames(Background.CONTRAST_5, Display.FLEX, FlexDirection.COLUMN, AlignItems.START, Padding.MEDIUM,
                BorderRadius.LARGE);

        Div div = new Div();
        div.addClassNames(Background.CONTRAST, Display.FLEX, AlignItems.CENTER, JustifyContent.CENTER,
                Margin.Bottom.MEDIUM, Overflow.HIDDEN, BorderRadius.MEDIUM, Width.FULL);
        div.setHeight("160px");

        // Deterministically select an image URL based on the event's ID
        // This ensures the same event always gets the same image
        String imageUrl = IMAGE_URLS[Math.abs(event.getId().hashCode()) % IMAGE_URLS.length];

        Image image = new Image();
        image.setWidth("100%");
        image.setSrc(imageUrl);
        image.setAlt(event.getName());

        div.add(image);

        Span header = new Span();
        header.addClassNames(FontSize.XLARGE, FontWeight.SEMIBOLD);
        header.setText(event.getName());

        Span organizer = new Span();
        organizer.addClassNames(FontSize.SMALL, TextColor.SECONDARY);
        organizer.setText("Organized by: " + event.getOrganiser().getName());

        Paragraph dateInfo = new Paragraph("Date: " + event.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        dateInfo.addClassName(Margin.Vertical.MEDIUM);

        // Add place information
        Paragraph placeInfo = new Paragraph();
        if (event.getPlace() != null) {
            placeInfo.setText("Location: " + event.getPlace().getName() + ", " +
                    event.getPlace().getAddress() + ", " +
                    event.getPlace().getCity());
        } else {
            placeInfo.setText("Location: TBA");
        }
        placeInfo.addClassName(Margin.Bottom.MEDIUM);

        Span instruments = new Span();
        instruments.addClassNames(FontSize.SMALL);
        String instrumentText = event.getInstruments().stream()
                .map(Instrument::getName)
                .collect(Collectors.joining(", "));
        instruments.setText("Instruments: " + instrumentText);

        add(div, header, organizer, dateInfo, placeInfo, instruments);
    }
}