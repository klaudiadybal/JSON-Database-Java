package client;

import com.beust.jcommander.Parameter;

public class Args {
    @Parameter (names = "-t", description = "Type of request")
    public String type;

    @Parameter (names = "-k", description = "Index of cell")
    public int index;

    @Parameter (names = "-v", description = "Text to store in server")
    public String text;

}
