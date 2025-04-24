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

    private static final String[] IMAGE_URLS = {
            "https://cdn.pixabay.com/photo/2016/09/08/21/09/piano-1655558_1280.jpg",
            "https://cdn.pixabay.com/photo/2016/09/08/21/09/piano-1655558_1280.jpg",
            "https://cdn.pixabay.com/photo/2022/05/24/19/28/cello-7219171_1280.jpg",
            "https://cdn.pixabay.com/photo/2016/11/23/14/48/bowed-instrument-1853324_1280.jpg",
            "https://cdn.pixabay.com/photo/2017/11/12/16/41/musician-2943109_1280.jpg",
            "https://cdn.pixabay.com/photo/2018/01/18/12/39/music-3090204_1280.jpg",
            "https://cdn.pixabay.com/photo/2020/08/06/16/35/live-performance-5468470_1280.jpg",
            "https://cdn.pixabay.com/photo/2016/11/19/09/57/violins-1838390_1280.jpg",
            "https://cdn.pixabay.com/photo/2017/02/25/22/05/orchestra-2098877_1280.jpg",
            "https://cdn.pixabay.com/photo/2020/05/11/15/54/bass-5158902_1280.jpg",
            "https://cdn.pixabay.com/photo/2020/12/09/18/42/violin-5818267_1280.jpg"
    };

    public EventsViewCard(Event event) {
        addClassNames(Background.CONTRAST_5, Display.FLEX, FlexDirection.COLUMN, AlignItems.START, Padding.MEDIUM,
                BorderRadius.LARGE);

        Div div = new Div();
        div.addClassNames(Background.CONTRAST, Display.FLEX, AlignItems.CENTER, JustifyContent.CENTER,
                Margin.Bottom.MEDIUM, Overflow.HIDDEN, BorderRadius.MEDIUM, Width.FULL);
        div.setHeight("160px");

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