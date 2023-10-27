package client;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

public class Args {
    @Parameter (names = "-t", description = "Type of request", required = true, validateWith = TypeValidator.class)
    public String type;

    @Parameter (names = "-k", description = "Index of cell")
    public String index;

    @Parameter(names = "-v", description = "Text to store in server")
    public String text;

    public static class TypeValidator implements IParameterValidator {
        public void validate(String name, String value) throws ParameterException {
            if (!value.equals("set") && !value.equals("get") && !value.equals("delete") && !value.equals("exit")) {
                throw new ParameterException("ERROR: Invalid value for -t parameter: " + value);
            }
        }
    }
}
