package com.example.application.views;

import com.example.application.data.entities.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.UserService;
import com.example.application.views.login.RegisterComponent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.page.WebStorage;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * The main view is a top-level placeholder for other views.
 */
@Layout
@AnonymousAllowed
public class MainLayout extends AppLayout implements LocaleChangeObserver {

    private H1 viewTitle;
    private Span appName;
    private SideNav nav;
    private Scroller scroller;
    private final AuthenticatedUser authenticatedUser;
    private AccessAnnotationChecker accessChecker;

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final I18NProvider provider;

    private Div copyrightFooter;


    public MainLayout(AuthenticatedUser authenticatedUser,
                      AccessAnnotationChecker accessChecker,
                      UserService userService,
                      PasswordEncoder passwordEncoder, I18NProvider provider) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.provider = provider;

        WebStorage.getItem("locale", value -> {
            if (value != null)
                UI.getCurrent().setLocale(Locale.forLanguageTag(value));
        });

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();

        addCopyrightFooter();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        appName = new Span(getTranslation("myapp"));
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);
        Header header = new Header(appName);

        scroller = new Scroller(createNavigation());
        nav = createNavigation();
        scroller.setContent(nav);


        addToDrawer(header, scroller, createFooter());
    }

    private void addCopyrightFooter() {
        copyrightFooter = new Div();
        copyrightFooter.add(new Span("© 2025 EventApp, All Rights Reserved"));

        copyrightFooter.getStyle()
                .set("width", "100%")
                .set("padding", "0.5em 0")
                .set("text-align", "center")
                .set("font-size", "12px")
                .set("color", "gray")
                .set("position", "fixed")
                .set("bottom", "0")
                .set("background-color", "white")
                .set("border-top", "1px solid #e0e0e0");

        getElement().appendChild(copyrightFooter.getElement());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        List<MenuEntry> menuEntries = MenuConfiguration.getMenuEntries();
        menuEntries.forEach(entry -> {
            String translatedTitle = getTranslation("nav." + entry.title().toLowerCase().replace(" ", "."));

            if (entry.icon() != null) {
                nav.addItem(new SideNavItem(translatedTitle, entry.path(), new SvgIcon(entry.icon())));
            } else {
                nav.addItem(new SideNavItem(translatedTitle, entry.path()));
            }
        });

        return nav;
    }


    private Footer createFooter() {
        Footer layout = new Footer();

        //kielivalinnat
        for (Locale locale : provider.getProvidedLocales()){
            Button button = new Button(locale.getDisplayLanguage(), e -> {
                UI.getCurrent().setLocale(locale);
                WebStorage.setItem("locale", locale.toLanguageTag());
            });
            layout.add(button);
        }

        layout.getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("gap", "var(--lumo-space-s)");

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();

            Avatar avatar = new Avatar(user.getName());
            StreamResource resource = new StreamResource("profile-pic",
                    () -> new ByteArrayInputStream(user.getProfilePicture()));
            avatar.setImageResource(resource);
            avatar.setThemeName("xsmall");
            avatar.getElement().setAttribute("tabindex", "-1");

            MenuBar userMenu = new MenuBar();
            userMenu.setThemeName("tertiary-inline contrast");

            MenuItem userName = userMenu.addItem("");
            Div div = new Div();
            div.add(avatar);
            div.add(user.getName());
            div.add(new Icon("lumo", "dropdown"));
            div.addClassNames(LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER, LumoUtility.Gap.SMALL);
            userName.add(div);
            userName.getSubMenu().addItem("Sign out", e -> {
                authenticatedUser.logout();
            });

            layout.add(userMenu);
        } else {
            Anchor loginLink = new Anchor("login", "Sign in");
            Button registerButton = new Button("Register");
            RegisterComponent registerComponent = new RegisterComponent(userService, passwordEncoder);
            registerButton.addClickListener(e -> registerComponent.openRegisterComponent());
            layout.add(loginLink, registerButton, registerComponent);
        }

        // Copyright on nyt omassa metodissa
        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        String translatedTitle = getCurrentPageTitle();
        viewTitle.setText(translatedTitle);
        UI.getCurrent().getPage().setTitle(translatedTitle);

        getContent().getElement().getStyle().set("padding-bottom", "40px");
    }


    private String getCurrentPageTitle() {
        return Optional.ofNullable(getContent())
                .map(Component::getClass)
                .map(clazz -> clazz.getAnnotation(PageTitle.class))
                .map(PageTitle::value)
                .map(key -> getTranslation(key))
                .orElse("");
    }


    @Override
    public void localeChange(LocaleChangeEvent localeChangeEvent) {
        appName.setText(getTranslation("myapp"));
        nav.removeAll();
        List<MenuEntry> menuEntries = MenuConfiguration.getMenuEntries();
        for (MenuEntry entry : menuEntries) {
            String translatedTitle = getTranslation("nav." + entry.title().toLowerCase().replace(" ", "."));
            if (entry.icon() != null) {
                nav.addItem(new SideNavItem(translatedTitle, entry.path(), new SvgIcon(entry.icon())));
            } else {
                nav.addItem(new SideNavItem(translatedTitle, entry.path()));
            }
        }

        String translatedTitle = getCurrentPageTitle();
        viewTitle.setText(translatedTitle);
        UI.getCurrent().getPage().setTitle(translatedTitle);
    }
}