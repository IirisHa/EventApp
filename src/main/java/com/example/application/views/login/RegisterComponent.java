package com.example.application.views.login;

import com.example.application.data.entities.User;
import com.example.application.services.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class RegisterComponent extends Div {

    private Dialog dialog;

    public RegisterComponent(UserService userService, PasswordEncoder passwordEncoder){
        dialog = new Dialog();
        User user = new User();

        dialog.setHeaderTitle("New user");

        FormLayout formLayout = new FormLayout();
        TextField username = new TextField("username");
        TextField name = new TextField("Name");
        TextField email = new TextField("Email");
        MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
        Upload profilePicture = new Upload(buffer);
        profilePicture.setMaxFiles(1);

        profilePicture.setAcceptedFileTypes("image/*"); // yksi tai useampi MIME määritys
        profilePicture.addSucceededListener(event -> {
            String filename = event.getFileName();
            InputStream inputStream = buffer.getInputStream(filename);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            int bytesRead;
            while (true) {
                try {
                    if ((bytesRead = inputStream.read(bytes)) == -1) break;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                byteArrayOutputStream.write(bytes,0,bytesRead);
            }
            user.setProfilePicture(byteArrayOutputStream.toByteArray());
        });
        PasswordField password = new PasswordField("Password");
        PasswordField confirmPassword = new PasswordField("Confirm password");
        formLayout.add(name, username, email, profilePicture, password, confirmPassword);
        dialog.add(formLayout);
        Button saveButton = new Button("Save");
        Button cancelButton = new Button("Cancel");
        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(saveButton);

        add(dialog);

        BeanValidationBinder<User> binder = new BeanValidationBinder<>(User.class);

        binder.forField(username).asRequired("Pakollinen kenttä")
                .withValidator(userService::userNameAvailable,
                        "Käyttäjänimi on varattu")
                .bind(User::getUsername, User::setUsername);
        binder.forField(name).asRequired("Pakollinen kenttä")
                .bind(User::getName, User::setName);
        binder.forField(email).asRequired("Pakollinen kenttä")
                .bind(User::getEmail, User::setEmail);
        binder.forField(password).asRequired("Pakollinen kenttä")
                .withValidator(pw -> pw.length() >= 8,
                        "Salasanan pitää olla vähintään 8 merkkiä")
                .bind(User::getHashedPassword,
                        (user1, pw) -> user1.setHashedPassword(passwordEncoder.encode(pw)));
        binder.forField(confirmPassword).asRequired("Pakollinen kenttä")
                .withValidator(confirmed -> Objects.equals(confirmed, password.getValue()),
                        "Salasanojen täytyy olla samat")
                .bind(User::getHashedPassword,
                        (user1, pw) -> user1.setHashedPassword(passwordEncoder.encode(pw)));

        saveButton.addClickListener(e -> {
            binder.validate();
            if (binder.isValid()){
                try {
                    binder.writeBean(user);
                    userService.save(user);
                    UI.getCurrent().navigate("login");
                } catch (ValidationException ex) {
                    throw new RuntimeException(ex);
                }
            }

        });

        cancelButton.addClickListener( e -> {
            name.clear();
            username.clear();
            password.clear();
            email.clear();
            confirmPassword.clear();
            dialog.close();
        });
    }

    public void openRegisterComponent(){
        dialog.open();
    }
}

