package com.example.application.views.login;

import com.example.application.security.AuthenticatedUser;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@AnonymousAllowed
@PageTitle("page.title.login")
@Route(value = "login")
public class LoginView extends LoginOverlay implements BeforeEnterObserver {
    private final AuthenticatedUser authenticatedUser;

    public LoginView(AuthenticatedUser authenticatedUser) {
            this.authenticatedUser = authenticatedUser;

        setAction(RouteUtil.getRoutePath(VaadinService.getCurrent().getContext(), getClass()));

        LoginI18n i18n = new LoginI18n();
        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("Tapahtumat.fi");
        i18n.getHeader().setDescription("Login using john/john1234, jane/jane1234, bob/bob12345 or emma/admin1234");
        i18n.setAdditionalInformation(null);

        // Kenttien ja napin tekstien asettaminen
        LoginI18n.Form form = new LoginI18n.Form();
        form.setTitle("Kirjaudu sisään");
        form.setUsername("Käyttäjätunnus");
        form.setPassword("Salasana");
        form.setSubmit("Kirjaudu");
        form.setForgotPassword("");
        i18n.setForm(form);

        LoginI18n.ErrorMessage errorMessage = new LoginI18n.ErrorMessage();
        errorMessage.setTitle("Virheellinen käyttäjätunnus tai salasana");
        errorMessage.setMessage("Tarkista syöttämäsi tiedot ja yritä uudelleen.");
        i18n.setErrorMessage(errorMessage);

        setI18n(i18n);
        setForgotPasswordButtonVisible(false);
        setOpened(true);

    }
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticatedUser.get().isPresent()) {
            setOpened(false);
            event.rerouteTo("");
        }
        setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }
}

