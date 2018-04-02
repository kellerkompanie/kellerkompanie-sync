package com.kellerkompanie.kekosync.client.gui;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class ModItem {
    @Getter
    @Setter
    private boolean checked = true;
    @Getter
    @Setter
    private String name = "mod";
    @Getter
    @Setter
    private Status status = Status.OK;

    enum Status {
        OK, INCOMPLETE, MISSING
    }
}
