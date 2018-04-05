package com.kellerkompanie.kekosync.client.arma;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Objects;

/**
 * @author Schwaggot
 */
public class ArmAParameter implements Serializable {

    public static final String SHOW_SCRIPT_ERRORS = "SHOW_SCRIPT_ERRORS";
    public static final String NO_PAUSE = "NO_PAUSE";
    public static final String WINDOW_MODE = "WINDOW_MODE";
    public static final String FILE_PATCHING = "FILE_PATCHING";
    public static final String CHECK_SIGNATURES = "CHECK_SIGNATURES";
    public static final String NO_SPLASH = "NO_SPLASH";
    public static final String DEFUALT_WORLD_EMPTY = "DEFUALT_WORLD_EMPTY";
    public static final String NO_LOGS = "NO_LOGS";
    public static final String CPU_COUNT = "CPU_COUNT";
    public static final String EX_THREADS = "EX_THREADS";

    public static final String PORT= "PORT";
    public static final String SERVER= "SERVER";
    public static final String PASSWORD= "PASSWORD";

    private final ParameterType type;
    private String argument;
    @Getter
    @Setter
    private boolean enabled;
    @Getter
    @Setter
    private String value;

    public ArmAParameter(String argument, boolean enabled) {
        this(ParameterType.BOOLEAN, argument, enabled);
    }

    public ArmAParameter(ParameterType parameterType, String argument, boolean enabled) {
        this.type = parameterType;
        this.argument = argument;
        this.enabled = enabled;
    }

    public ArmAParameter(String argument, boolean enabled, String value) {
        this(ParameterType.COMBO, argument, enabled);
        this.value = value;
    }

    public static HashMap<String, ArmAParameter> getDefaultParameters() {
        ArmAParameter showScriptErrorsParam = new ArmAParameter("-showScriptErrors", true);
        ArmAParameter noPauseParamm = new ArmAParameter("-noPause", false);
        ArmAParameter windowModeParam = new ArmAParameter("-window", false);
        ArmAParameter filePatchingParam = new ArmAParameter("-filePatching", false);
        ArmAParameter checkSignaturesParam = new ArmAParameter("-checkSignatures", false);
        ArmAParameter noSplashParam = new ArmAParameter("-nosplash", true);
        ArmAParameter worldEmptyParam = new ArmAParameter("-world=empty", true);
        ArmAParameter noLogsParam = new ArmAParameter("-nologs", false);

        ArmAParameter cpuCountParam = new ArmAParameter("-cpuCount", false, "1");
        ArmAParameter threadsParam = new ArmAParameter("-exThreads", false, "0");

        ArmAParameter portParam = new ArmAParameter("-port", false, "2302");
        ArmAParameter serverParam = new ArmAParameter("-connect", false, "server.kellerkompanie.com");
        ArmAParameter passwordParam = new ArmAParameter("-password", false, "keko");

        HashMap<String, ArmAParameter> defaultParams = new HashMap<>();
        defaultParams.put(SHOW_SCRIPT_ERRORS, showScriptErrorsParam);
        defaultParams.put(NO_PAUSE, noPauseParamm);
        defaultParams.put(WINDOW_MODE, windowModeParam);
        defaultParams.put(FILE_PATCHING, filePatchingParam);
        defaultParams.put(CHECK_SIGNATURES, checkSignaturesParam);
        defaultParams.put(NO_SPLASH, noSplashParam);
        defaultParams.put(DEFUALT_WORLD_EMPTY, worldEmptyParam);
        defaultParams.put(NO_LOGS, noLogsParam);
        defaultParams.put(CPU_COUNT, cpuCountParam);
        defaultParams.put(EX_THREADS, threadsParam);
        defaultParams.put(PORT, portParam);
        defaultParams.put(SERVER, serverParam);
        defaultParams.put(PASSWORD, passwordParam);

        return defaultParams;
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

    public ParameterType getType() {
        return type;
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
