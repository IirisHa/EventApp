package com.example.application.views.events;

import com.example.application.data.entities.Event;
import com.example.application.data.entities.Instrument;
import com.example.application.data.entities.Place;
import com.example.application.services.EventService;
import com.example.application.services.InstrumentService;
import com.example.application.services.PlaceService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.ListStyleType;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.MaxWidth;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import com.vaadin.flow.theme.lumo.LumoUtility.Width;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@PageTitle("page.title.events")
@Route("events")
@Menu(order = 1, icon = LineAwesomeIconUrl.FILTER_SOLID)
@AnonymousAllowed
@Uses(Icon.class)
public class EventsView extends Div {

    private OrderedList eventsContainer;
    private Filters filters;
    private final EventService eventService;
    private final InstrumentService instrumentService;
    private final PlaceService placeService;

    public EventsView(EventService eventService, InstrumentService instrumentService, PlaceService placeService) {
        this.eventService = eventService;
        this.instrumentService = instrumentService;
        this.placeService = placeService;
        setSizeFull();
        addClassNames("events-view", MaxWidth.SCREEN_LARGE, Margin.Horizontal.AUTO);

        filters = new Filters(() -> refreshEvents());
        constructUI();
        loadEvents();

        UI.getCurrent().getPage().setTitle(getTranslation("page.title.events"));
    }

    private void constructUI() {
        addClassNames(Padding.LARGE);

        HorizontalLayout headerContainer = new HorizontalLayout();
        headerContainer.addClassNames(AlignItems.CENTER, JustifyContent.BETWEEN, Width.FULL);

        VerticalLayout headerTextContainer = new VerticalLayout();
        headerTextContainer.setPadding(false);
        headerTextContainer.setSpacing(false);

        H2 header = new H2("Upcoming Events");
        header.addClassNames(Margin.Bottom.NONE, Margin.Top.XLARGE, FontSize.XXXLARGE);

        Paragraph description = new Paragraph("Find and join exciting musical events near you");
        description.addClassNames(Margin.Bottom.XLARGE, Margin.Top.NONE, TextColor.SECONDARY);

        headerTextContainer.add(header, description);
        headerContainer.add(headerTextContainer);

        HorizontalLayout mobileFilters = createMobileFilters();

        eventsContainer = new OrderedList();
        eventsContainer.addClassNames(Gap.MEDIUM, Display.GRID, ListStyleType.NONE, Margin.NONE, Padding.NONE);
        eventsContainer.getStyle().set("grid-template-columns", "repeat(auto-fill, minmax(280px, 1fr))");

        add(headerContainer, mobileFilters, filters, eventsContainer);
    }

    private HorizontalLayout createMobileFilters() {
        // Mobile version
        HorizontalLayout mobileFilters = new HorizontalLayout();
        mobileFilters.setWidthFull();
        mobileFilters.addClassNames(LumoUtility.Padding.MEDIUM, LumoUtility.BoxSizing.BORDER,
                LumoUtility.AlignItems.CENTER);
        mobileFilters.addClassName("mobile-filters");

        Icon mobileIcon = new Icon("lumo", "plus");
        Span filtersHeading = new Span("Filters");
        mobileFilters.add(mobileIcon, filtersHeading);
        mobileFilters.setFlexGrow(1, filtersHeading);
        mobileFilters.addClickListener(e -> {
            if (filters.getClassNames().contains("visible")) {
                filters.removeClassName("visible");
                mobileIcon.getElement().setAttribute("icon", "lumo:plus");
            } else {
                filters.addClassName("visible");
                mobileIcon.getElement().setAttribute("icon", "lumo:minus");
            }
        });
        return mobileFilters;
    }

    public class Filters extends Div implements Specification<Event> {

        private final TextField name = new TextField("Event Name");
        private final TextField organiser = new TextField("Organiser");
        private final DatePicker date = new DatePicker("Date of Event");
        private final ComboBox<Instrument> instruments = new ComboBox<>("Instruments");
        private final ComboBox<String> city = new ComboBox<>("City");


        public Filters(Runnable onSearch) {
            setWidthFull();
            addClassName("filter-layout");
            instruments.setItems(instrumentService.findAll());
            instruments.setItemLabelGenerator(Instrument::getName);

            // Get all unique cities from places
            List<String> cities = placeService.findAll().stream()
                    .map(Place::getCity)
                    .distinct()
                    .collect(Collectors.toList());
            city.setItems(cities);

            // Action buttons
            Button resetBtn = new Button("Reset");
            resetBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            resetBtn.addClickListener(e -> {
                name.clear();
                organiser.clear();
                date.clear();
                instruments.clear();
                city.clear();
                onSearch.run();
            });

            Button searchBtn = new Button("Search");
            searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            searchBtn.addClickListener(e -> onSearch.run());

            Div actions = new Div(resetBtn, searchBtn);
            actions.addClassName(LumoUtility.Gap.SMALL);
            actions.addClassName("actions");

            add(name, organiser, createDateRangeFilter(), instruments, city, actions);
        }

        private Component createDateRangeFilter() {
            FlexLayout dateRangeComponent = new FlexLayout(date);
            dateRangeComponent.setAlignItems(FlexComponent.Alignment.BASELINE);
            dateRangeComponent.addClassName(LumoUtility.Gap.XSMALL);

            return dateRangeComponent;
        }

        @Override
        public Predicate toPredicate(Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by name
            if (name.getValue() != null && !name.getValue().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + name.getValue().toLowerCase() + "%"));
            }

            // Filter by organiser
            if (organiser.getValue() != null && !organiser.getValue().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.join("organiser").get("name")),
                        "%" + organiser.getValue().toLowerCase() + "%"));
            }

            // Filter by date
            if (date.getValue() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("date"), date.getValue()));
            }

            // Filter by instrument
            if (instruments.getValue() != null) {
                predicates.add(criteriaBuilder.isMember(
                        instruments.getValue(),
                        root.get("instruments")));
            }
            // Filter by city
            if (city.getValue() != null && !city.getValue().isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                        root.join("place").get("city"),
                        city.getValue()));
            }

            // If no predicates, return null (no filtering)
            if (predicates.isEmpty()) {
                return null;
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        }
    }

    private void loadEvents() {
        // Get filtered events using service
        List<Event> events = eventService.list(PageRequest.of(0, 100), filters).getContent();

        // Clear previous content
        eventsContainer.removeAll();

        // Add event cards
        if (events.isEmpty()) {
            Paragraph noResults = new Paragraph("No events found matching your criteria");
            noResults.addClassNames(Padding.MEDIUM, TextColor.SECONDARY);
            eventsContainer.add(noResults);
        } else {
            events.forEach(event -> eventsContainer.add(new EventsViewCard(event)));
        }
    }

    private void refreshEvents() {
        loadEvents();
        Notification.show("Events refreshed", 2000, Notification.Position.BOTTOM_START);
    }
}

