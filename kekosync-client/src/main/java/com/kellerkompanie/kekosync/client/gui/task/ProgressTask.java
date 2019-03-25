package com.kellerkompanie.kekosync.client.gui.task;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@EqualsAndHashCode(of = {"uuid"})
@ToString
public abstract class ProgressTask<Result> {
    private final UUID uuid;
    @Getter
    @Setter
    private ProgressTaskState state;

    ProgressTask() {
        uuid = UUID.randomUUID();
        state = ProgressTaskState.CREATED;
    }

    public void onPreExecute() {
    }

    public abstract Result doInBackground();

    public void onPostExecute(Result result) {
    }
}
