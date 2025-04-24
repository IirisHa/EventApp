package com.example.application.views.home;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("page.title.home")
@Route("")
@AnonymousAllowed
@Menu(order = 0, icon = LineAwesomeIconUrl.PENCIL_RULER_SOLID)
public class HomeView extends Composite<VerticalLayout> implements LocaleChangeObserver {

    private H1 header;
    private H1 otsikko;
    private H2 subHeader;

    public HomeView() {
        HorizontalLayout layoutRow = new HorizontalLayout();
        VerticalLayout layoutColumn2 = new VerticalLayout();
        HorizontalLayout layoutRow2 = new HorizontalLayout();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.setHeight("min-content");
        layoutColumn2.setWidth("100%");
        layoutColumn2.getStyle().set("flex-grow", "1");
        layoutRow2.addClassName(Gap.MEDIUM);
        layoutRow2.setWidth("100%");
        layoutRow2.setHeight("min-content");
        getContent().add(layoutRow);
        getContent().add(layoutColumn2);
        getContent().add(layoutRow2);

        // Otsikko 1: tyylit suoraan komponenttiin (2 pistettä)
        header = new H1(getTranslation("home.welcome"));
        header.getStyle().set("color", "darkgreen").set("fontSize", "2rem");

        otsikko = new H1(getTranslation("home.yourEvents"));
        otsikko.getStyle()
                .set("text-align", "center")
                .set("font-size", "3rem")
                .set("margin-top", "2rem")
                .set("letter-spacing", "0.2rem");

        Image kuva = new Image("/images/homepage.jpg", "Etusivun kuva");
        kuva.addClassName("hero-image");
        getContent().add(otsikko, kuva);


        // Otsikko 2: tyylit luokan kautta (3 pistettä)
        subHeader = new H2(getTranslation("home.subtitle"));
        subHeader.addClassName("tervetuloa-teksti");

        // Lumo Utility -luokkien käyttö (4 pistettä)
        VerticalLayout contentBlock = new VerticalLayout(header, subHeader);
        contentBlock.addClassNames(LumoUtility.Padding.LARGE, LumoUtility.Margin.MEDIUM, Gap.SMALL);

        layoutColumn2.add(contentBlock);
        getContent().add(layoutRow, layoutColumn2, layoutRow2);

        // Manually set the page title
        UI.getCurrent().getPage().setTitle(getTranslation("page.title.home"));

    }
    @Override
    public void localeChange(LocaleChangeEvent event) {
        // Update all text content when locale changes
        header.setText(getTranslation("home.welcome"));
        otsikko.setText(getTranslation("home.yourEvents"));
        subHeader.setText(getTranslation("home.subtitle"));
        // Manually set the page title
        UI.getCurrent().getPage().setTitle(getTranslation("page.title.home"));
    }
}
