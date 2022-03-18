package teplykh.logical.server;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import teplykh.logical.exceptions.UnknownCommand;
import teplykh.logical.exceptions.ValueException;
import teplykh.logical.ioValues.LogicInput;
import teplykh.logical.ioValues.LogicOutput;
import teplykh.logical.ioValues.interfaces.IOElement;
import teplykh.logical.logicalFunctions.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import org.apache.commons.lang3.StringUtils;
import teplykh.repository.ExpressionRepository;
import teplykh.repository.UserRepository;

@Component
public class Server {

    @Value(value = "${help}")
    private String help;

    @Autowired
    private HashMapLoaderSaver hashMapLoaderSaver;
    private HashMap<String, IOElement> elements;
    private LogicOutput output;

    @Setter
    private String curUser;

    public void init() {
        try {
            elements = hashMapLoaderSaver.readHashMap(curUser);
            for (IOElement element : elements.values()){
                if (element instanceof LogicOutput){
                    output = (LogicOutput) element;
                    break;
                }
            }
        } catch (Exception e) {
            hashMapLoaderSaver.createNewFile(curUser);
            elements = new HashMap<>();
            System.out.println("Created new file");
        }
    }

    @PreDestroy
    public void saveHashMap(){
        try {
            hashMapLoaderSaver.writeHashMap(elements, curUser);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int parseInt(String strNumber) throws ValueException {
        int result;
        try {
            result = Integer.parseInt(strNumber);
        } catch (Exception e) {
            throw new ValueException("Can't parse int");
        }
        return result;
    }

    private boolean parseBoolean(String strBool) throws ValueException {
        boolean result;
        try {
            result = Boolean.parseBoolean(strBool);
        } catch (Exception e) {
            throw new ValueException("Can't parse boolean");
        }
        return result;
    }

    public String parseAdd(String element, String name) throws UnknownCommand, ValueException {
        if (StringUtils.isBlank(element) || StringUtils.isBlank(name)) {
            throw new UnknownCommand("Unknown format of add command");
        }
        if (elements.containsKey(name) || output != null && output.getName().equals(name)) {
            throw new ValueException(String.format("There is logical element with name: \"%s\"", name));
        }
        if (element.equals(LogicInput.COMMAND_NAME)) {
            LogicInput input = new LogicInput(name);
            elements.put(input.getName(), input);
        } else if (element.equals(LogicOutput.COMMAND_NAME)) {
            if (output == null) {
                output = new LogicOutput(name);
            } else {
                throw new ValueException("Output has already been set");
            }
        } else if (element.equals(AND.COMMAND_NAME)) {
            AND and = new AND(name);
            elements.put(and.getName(), and);
        } else if (element.equals(NOT.COMMAND_NAME)) {
            NOT not = new NOT(name);
            elements.put(not.getName(), not);
        } else if (element.equals(OR.COMMAND_NAME)) {
            OR or = new OR(name);
            elements.put(or.getName(), or);
        } else if (element.equals(XOR.COMMAND_NAME)) {
            XOR xor = new XOR(name);
            elements.put(xor.getName(), xor);
        } else {
            throw new UnknownCommand("Unknown format of add command");
        }
        return "Success";

    }

    public String parseConnect(String outputName, String inputName, String inputNumber) throws UnknownCommand, ValueException {
        if (StringUtils.isBlank(outputName) || StringUtils.isBlank(inputName) || (inputNumber != null && inputNumber.isEmpty())) {
            throw new UnknownCommand("Unknown format of add command");
        }
        if (output != null && output.getName().equals(outputName)) {
            throw new ValueException(String.format("Can't connect output element \"%s\", it's final element", outputName));
        }
        if (!elements.containsKey(outputName)) {
            throw new ValueException(String.format("No logical elements with such name: \"%s\"", outputName));
        }
        if (!elements.containsKey(inputName) && !(output != null && output.getName().equals(inputName))) {
            throw new ValueException(String.format("No logical elements with such name: \"%s\"", inputName));
        }
        IOElement firstElement = elements.get(outputName);
        IOElement secondElement;
        if (output != null && output.getName().equals((inputName))) {
            secondElement = output;
        } else {
            secondElement = elements.get(inputName);
        }
        if (secondElement instanceof LogicInput) {
            throw new ValueException(String.format("Can't set input logical element with name \"%s\" to logical element", secondElement.getName()));
        }
        if (inputNumber != null) {
            if (secondElement instanceof TwoParamsElement) {
                int port = parseInt(inputNumber);
                if (port != 1 && port != 0) {
                    throw new UnknownCommand("Input port must be equal 0 or 1");
                }
                if (port == 0) {
                    ((TwoParamsElement) secondElement).setFirstInput(firstElement);
                } else {
                    ((TwoParamsElement) secondElement).setSecondInput(firstElement);
                }
            } else {
                throw new UnknownCommand(String.format("Element with name \"%s\"has no 2 input ports", secondElement.getName()));
            }
        } else {
            secondElement.setInput(firstElement);
        }
        return "Success";
    }

    public String parseSet(String name, String value) throws UnknownCommand, ValueException {
        if (StringUtils.isBlank(name) || StringUtils.isBlank(value)) {
            throw new UnknownCommand("Unknown format of set command");
        }
        if (!elements.containsKey(StringUtils.strip(name))) {
            throw new ValueException(String.format("No logical elements with such name: \"%s\"", name));
        }
        IOElement inputElement = elements.get(name);
        if (!(inputElement instanceof LogicInput)) {
            throw new ValueException(String.format("Logical element with name: \"%s\" is not input", name));
        }
        boolean input = parseBoolean(value);
        inputElement.setInput(input);
        return "Success";
    }

    public String parsePrint() throws ValueException {
        if (output == null) {
            throw new ValueException("Output element is not set");
        }
        return output.getOutput().toString();
    }

    public String parseShow(String name) throws UnknownCommand, ValueException {
        if (name != null && name.isEmpty()) {
            throw new UnknownCommand("Unknown format of show command");
        }
        if (name == null) {
            StringJoiner result = new StringJoiner(" ");
            for (Map.Entry<String, IOElement> entry : elements.entrySet()) {
                String key = entry.getKey();
                IOElement value = entry.getValue();
                result.add(value.getCommandName());
            }
            if (output != null) {
                result.add(output.getCommandName());
            }
            return result.toString();
        } else {
            if (!elements.containsKey(StringUtils.strip(name)) && !(output != null && output.getName().equals(name))) {
                throw new ValueException(String.format("No logical elements with such name: \"%s\"", name));
            }
            if (output != null && output.getName().equals(name)) {
                return output.getFullName();
            } else {
                return elements.get(name).getFullName();
            }
        }
    }

    public String getHelp() {
        return help;
    }
}
