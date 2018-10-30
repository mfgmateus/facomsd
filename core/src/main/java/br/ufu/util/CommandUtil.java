package br.ufu.util;

public class CommandUtil {

    private CommandUtil() {

    }

    public static String getAction(String command) {
        return command.split(Constants.COMMAND_SEPARATOR)[0];
    }

    public static String getKey(String command) {
        return command.split(Constants.COMMAND_SEPARATOR)[1];
    }
}
