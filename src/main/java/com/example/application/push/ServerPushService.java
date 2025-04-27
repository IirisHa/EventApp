package com.example.application.push;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import java.util.Timer;
import java.util.TimerTask;


public class ServerPushService {
    private Timer timer;
    private UI ui;

    public void startPush(UI ui) {
        this.ui = ui;

        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (ui != null) {
                    ui.access(() -> {
                        Notification notification = Notification.show("Server Push -päivitys: " + System.currentTimeMillis());
                        notification.setPosition(Notification.Position.TOP_END);

                    });
                }
            }
        }, 0, 5000);
    }

    public void stopPush() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}