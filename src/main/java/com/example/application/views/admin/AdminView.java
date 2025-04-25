package com.example.application.views.admin;

import com.example.application.data.entities.Event;
import com.example.application.data.entities.Instrument;
import com.example.application.data.entities.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.EventService;
import com.example.application.services.InstrumentService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@PageTitle("page.title.admin")
@Route("admin/events")
@Menu(order = 2, icon = LineAwesomeIconUrl.CALENDAR_SOLID)
//@RolesAllowed({"ADMIN"})
@Uses(Icon.class)
public class AdminView extends Div {

    private Grid<Event> grid;
    private final EventService eventService;
    private final InstrumentService instrumentService;
    private final AuthenticatedUser authenticatedUser;
    private User currentUser;

    public AdminView(EventService eventService, InstrumentService instrumentService, AuthenticatedUser authenticatedUser) {
        this.eventService = eventService;
        this.instrumentService = instrumentService;
        this.authenticatedUser = authenticatedUser;

        setSizeFull();
        addClassNames("admin-events-view");

        currentUser = authenticatedUser.get().orElse(null);
        if (currentUser == null) {
            add(new Div("You are not logged in."));
            return;
        }

        VerticalLayout layout = new VerticalLayout(createHeader(),createGrid());
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        add(layout);

        // Load initial data
        refreshGrid();
    }
    private Component createHeader() {
        H2 pageTitle = new H2("All Events");
        pageTitle.addClassNames(LumoUtility.Margin.NONE);

        HorizontalLayout headerLayout = new HorizontalLayout(pageTitle);
        headerLayout.setWidthFull();
        headerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        headerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        headerLayout.addClassNames(LumoUtility.Padding.MEDIUM);

        return headerLayout;
    }

    private Component createGrid() {
        grid = new Grid<>(Event.class, false);
        grid.addColumn(Event::getName)
                .setHeader("Event Name")
                .setAutoWidth(true);

        grid.addColumn(event -> event.getOrganiser() != null ? event.getOrganiser().getName() : "")
                .setHeader("Organiser")
                .setAutoWidth(true);

        grid.addColumn(event -> event.getDate() != null ?
                        event.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "")
                .setHeader("Date")
                .setAutoWidth(true);

        grid.addColumn(event -> event.getInstruments() != null ?
                        event.getInstruments().stream()
                                .map(Instrument::getName)
                                .reduce((a, b) -> a + ", " + b)
                                .orElse("") : "")
                .setHeader("Instruments")
                .setAutoWidth(true);

        // Add edit and delete buttons
        grid.addComponentColumn(event -> {
            HorizontalLayout buttons = new HorizontalLayout();

            Button editButton = new Button("Edit");
            editButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            editButton.addClickListener(e -> showEventForm(event));

            Button deleteButton = new Button("Delete");
            deleteButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            deleteButton.addClickListener(e -> {
                eventService.delete(event.getId());
                refreshGrid();
            });

            buttons.add(editButton, deleteButton);
            return buttons;
        }).setHeader("Actions").setAutoWidth(true);

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);
        grid.setSizeFull();

        return grid;
    }

    private void showEventForm(Event event) {
        Dialog dialog = new Dialog();
        dialog.setWidth("600px");
        dialog.setHeaderTitle(event.getId() == null ? "Add New Event" : "Edit Event");

        TextField name = new TextField("Event Name");
        name.setWidthFull();

        TextField organiserName = new TextField("Organiser");
        organiserName.setWidthFull();

        DatePicker date = new DatePicker("Date");
        date.setWidthFull();

        MultiSelectComboBox<Instrument> instruments = new MultiSelectComboBox<>("Instruments");
        instruments.setItems(instrumentService.findAll());
        instruments.setItemLabelGenerator(Instrument::getName);
        instruments.setWidthFull();

        FormLayout formLayout = new FormLayout();
        formLayout.add(name, organiserName, date, instruments);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        formLayout.setColspan(instruments, 1);

        // Create binder
        Binder<Event> binder = new Binder<>(Event.class);

        binder.forField(name)
                .asRequired("Event name is required")
                .bind(Event::getName, Event::setName);

        binder.forField(date)
                .asRequired("Date is required")
                .bind(Event::getDate, Event::setDate);

        binder.forField(instruments)
                .bind(
                        event1 -> event1.getInstruments() != null ?
                                new HashSet<>(event1.getInstruments()) : new HashSet<>(),
                        (event1, selected) -> event1.setInstruments(new ArrayList<>(selected))
                );

        binder.forField(organiserName).bind(
                event1 -> event1.getOrganiser() != null ? event1.getOrganiser().getName() : "",
                (event1, value) -> {
                    if (event1.getOrganiser() == null) {
                        event1.setOrganiser(new User());
                    }
                    event1.getOrganiser().setName(value);
                }
        );

        // Buttons
        Button saveButton = new Button("Save");
        saveButton.addClickListener(e -> {
            if (binder.writeBeanIfValid(event)) {
                event.setOwner(currentUser);
                event.setOrganiser(currentUser);
                Event savedEvent = eventService.save(event);
                System.out.println("Saved event with ID: " + savedEvent.getId() +
                        " for user ID: " + currentUser.getId());
                dialog.close();
                refreshGrid();
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(e -> dialog.close());

        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setWidthFull();

        // Bind data to form
        binder.readBean(event);

        VerticalLayout dialogLayout = new VerticalLayout(formLayout, buttonLayout);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(true);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "100%");

        dialog.add(dialogLayout);
        dialog.open();
    }

    private void refreshGrid() {
        grid.setItems(eventService.findAll());
    }
}
