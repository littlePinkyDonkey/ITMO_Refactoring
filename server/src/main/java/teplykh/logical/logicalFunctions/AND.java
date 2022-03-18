package teplykh.logical.logicalFunctions;


import teplykh.logical.exceptions.ValueException;

public class AND extends TwoParamsElement {

    public final static String COMMAND_NAME = "and";

    public AND(String name) {
        super(name);
    }

    @Override
    public Boolean getOutput() throws ValueException {
        if(firstInput == null){
            throw new ValueException("");
        }
        if(secondInput == null){
            throw new ValueException("");
        }
        return firstInput.getOutput() && secondInput.getOutput();
    }

    @Override
    public String getCommandName() {
        return String.format("%s:%s", name, COMMAND_NAME);
    }
}
