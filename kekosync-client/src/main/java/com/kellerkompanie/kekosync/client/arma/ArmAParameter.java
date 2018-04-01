package com.kellerkompanie.kekosync.client.arma;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Schwaggot
 */
public class ArmAParameter {

    private final ParameterType type;
    private String description;
    private String argument;
    private boolean enabled;
    private String[] values;
    private String value;

    public ArmAParameter(String argument, String description, boolean enabled) {
        this(ParameterType.BOOLEAN, argument, description, enabled);
    }

    public ArmAParameter(ParameterType parameterType, String argument, String description, boolean enabled) {
        this.type = parameterType;
        this.description = description;
        this.argument = argument;
        this.enabled = enabled;
    }

    public ArmAParameter(String argument, String description, boolean enabled, String[] values, String value) {
        this(ParameterType.COMBO, argument, description, enabled);
        this.values = values;
        this.value = value;
    }

    public static List<ArmAParameter> getDefaultParameters() {
        ArmAParameter SHOWSCRIPT_ERRORS = new ArmAParameter("-showScriptErrors", "show script errors", true);
        ArmAParameter NO_PAUSE = new ArmAParameter("-noPause", "no pause", false);
        ArmAParameter WINDOW_MODE = new ArmAParameter("-window", "window mode", false);
        ArmAParameter FILE_PATCHING = new ArmAParameter("-filePatching", "file patching", false);
        ArmAParameter CHECK_SIGNATURES = new ArmAParameter("-checkSignatures", "check signatures", false);
        ArmAParameter NO_SPLASH = new ArmAParameter("-nosplash", "no splash screen", true);
        ArmAParameter DEFUALT_WORLD_EMPTY = new ArmAParameter("-world=empty", "default world empty", true);
        ArmAParameter NO_LOGS = new ArmAParameter("-nologs", "no logs", false);

        ArmAParameter CPU_COUNT = new ArmAParameter("-cpuCount", "CPU count", false, new String[]{"1", "2", "3", "4", "5", "6", "7", "8"}, "1");
        ArmAParameter EX_THREADS = new ArmAParameter("-exThreads", "ExThreads", false, new String[]{"0", "1", "3", "5", "7"}, "0");


        List<ArmAParameter> defaultParams = new ArrayList<ArmAParameter>();
        defaultParams.add(SHOWSCRIPT_ERRORS);
        defaultParams.add(NO_PAUSE);
        defaultParams.add(WINDOW_MODE);
        defaultParams.add(FILE_PATCHING);
        defaultParams.add(CHECK_SIGNATURES);
        defaultParams.add(NO_SPLASH);
        defaultParams.add(DEFUALT_WORLD_EMPTY);
        defaultParams.add(NO_LOGS);
        defaultParams.add(CPU_COUNT);
        defaultParams.add(EX_THREADS);
        return defaultParams;
    }

    public String getDescription() {
        return description;
    }

    public String getArgument() {
        switch (type) {
            case BOOLEAN:
                return argument;
            case COMBO:
                return argument + "=" + value;
            default:
                throw new IllegalStateException("type of parameter is not supported: " + type);
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public ParameterType getType() {
        return type;
    }

    public String[] getValues() {
        return values;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        if (enabled)
            return "[enabled]  " + getArgument();
        else
            return "[disabled] " + getArgument();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArmAParameter that = (ArmAParameter) o;
        return Objects.equals(argument, that.argument);
    }

    @Override
    public int hashCode() {

        return Objects.hash(argument);
    }

    public enum ParameterType {
        BOOLEAN, COMBO
    }
}
