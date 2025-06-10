package com.me.candyReport.managers;

import com.me.candyReport.notifier.LocalNotifier;
import com.me.candyReport.notifier.NetworkNotifier;

public class NotificationManager {

    private final LocalNotifier localNotifier;
    private final NetworkNotifier networkNotifier;

    public NotificationManager(LocalNotifier localNotifier, NetworkNotifier networkNotifier) {
        this.localNotifier = localNotifier;
        this.networkNotifier = networkNotifier;
    }

    public void notifyStaff(String reportedPlayer, String reporter, String reason) {
        localNotifier.notify(reportedPlayer, reporter, reason);
        networkNotifier.notify(reportedPlayer, reporter, reason);
    }




}
