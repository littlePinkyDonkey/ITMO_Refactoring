package util;

import exception.BusinessException;

import java.util.ArrayList;
import java.util.HashMap;

public class CommandParser {
    private HashMap<String, ArrayList<Integer>> commands;

    public CommandParser(final HashMap<String, ArrayList<Integer>> commands) {
        this.commands = commands;
    }

    public String parseCommand(final String line) throws BusinessException {
        String[] parsedCommands = line.split(" ");
        if (parsedCommands.length == 0) {
            throw new BusinessException("bad command");
        }
        if (commands.containsKey(parsedCommands[0])) {
            if (parsedCommands.length - 1 < commands.get(parsedCommands[0]).get(0)
                    || parsedCommands.length - 1 > commands.get(parsedCommands[0]).get(1)) {
                throw new BusinessException("bad command");
            }
        } else {
            throw new BusinessException("bad command");
        }
        return String.join("/", parsedCommands);
    }
}
