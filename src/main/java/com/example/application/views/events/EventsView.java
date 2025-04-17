package com.example.application.views.events;

import com.example.application.data.entities.Event;
import com.example.application.data.entities.Instrument;
import com.example.application.data.entities.User;
import com.example.application.services.EventService;
import com.example.application.services.InstrumentService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@PageTitle("Events")
@Route("grid-with-filters")
@Menu(order = 1, icon = LineAwesomeIconUrl.FILTER_SOLID)
@AnonymousAllowed
@Uses(Icon.class)
public class EventsView extends Div {

    private Grid<Event> grid;
    private Filters filters;
    private final EventService eventService;
    private final InstrumentService instrumentService;

    public EventsView(EventService eventService, InstrumentService instrumentService) {
        this.eventService = eventService;
        this.instrumentService = instrumentService;
        setSizeFull();
        addClassNames("events-view");

        filters = new Filters(() -> refreshGrid());
        VerticalLayout layout = new VerticalLayout(createMobileFilters(), filters, createGrid());
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        add(layout);
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

        public Filters(Runnable onSearch) {
            setWidthFull();
            addClassName("filter-layout");
            instruments.setItems(instrumentService.findAll());
            instruments.setItemLabelGenerator(Instrument::getName);

            // Action buttons
            Button resetBtn = new Button("Reset");
            resetBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            resetBtn.addClickListener(e -> {
                name.clear();
                organiser.clear();
                date.clear();
                instruments.clear();
                onSearch.run();
            });

            Button searchBtn = new Button("Search");
            searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            searchBtn.addClickListener(e -> onSearch.run());

            Div actions = new Div(resetBtn, searchBtn);
            actions.addClassName(LumoUtility.Gap.SMALL);
            actions.addClassName("actions");

            add(name, organiser, createDateRangeFilter(), instruments, actions);
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

            // Filter by instrument -> suodatus relaatiossa olevan entiteetin osalta
            if (instruments.getValue() != null) {
                predicates.add(criteriaBuilder.isMember(
                        instruments.getValue(),
                        root.get("instruments")));
            }

            // If no predicates, return null (no filtering)
            if (predicates.isEmpty()) {
                return null;
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        }
    }

    private Component createGrid() {
        grid = new Grid<>(Event.class, false);

        // Define columns with appropriate data extraction
        grid.addColumn(Event::getName)
                .setHeader("Event Name")
                .setAutoWidth(true);

        grid.addColumn(event -> event.getOrganiser().getName())
                .setHeader("Organiser")
                .setAutoWidth(true);

        grid.addColumn(event -> event.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .setHeader("Date")
                .setAutoWidth(true);

        grid.addColumn(event -> event.getInstruments().stream()
                        .map(Instrument::getName)
                        .collect(Collectors.joining(", ")))
                .setHeader("Instruments")
                .setAutoWidth(true);

        // Configure item source using the service with filters
        grid.setItems(query -> eventService.list(
                VaadinSpringDataHelpers.toSpringPageRequest(query),
                filters).stream());

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);

        return grid;
    }

    private void refreshGrid() {
        grid.getDataProvider().refreshAll();
    }
}