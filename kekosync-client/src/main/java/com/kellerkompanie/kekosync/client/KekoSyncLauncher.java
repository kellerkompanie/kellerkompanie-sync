package com.kellerkompanie.kekosync.client;

import com.kellerkompanie.kekosync.client.arma.ArmALauncher;
import com.kellerkompanie.kekosync.client.arma.ArmAParameter;
import com.kellerkompanie.kekosync.client.gui.RootController;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.util.LinkedList;

/**
 * @author Schwaggot
 */
public class KekoSyncLauncher {

    public static void main(String[] args) {
        RootController.openWindow(args);
    }

    private void startGame(ActionEvent event) {
        ArmALauncher armALauncher = new ArmALauncher(Settings.getArma3Executable());
        try {
            armALauncher.startArmA(new LinkedList<ArmAParameter>());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
