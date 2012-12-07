package converter;

import java.io.IOException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class MainClass {

    private static void usage(final Options options) {
        // Use the inbuilt formatter class
        final HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("JUnit4Converter", options);
    }

    public static void main(final String[] args) {
        final Options options = new Options();
        options.addOption(OptionBuilder
                            .withArgName("input file name")
                            .hasArg()
                            .withDescription("JUnit3 java filename")
                            .create("f")
                         );
        options.addOption("i", false, "in-place (edit files in place)");

        final CommandLineParser parser = new BasicParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
            if (!cmd.hasOption("f"))
                throw new ParseException("missing file name");
        }
        catch (final ParseException pe) {
            usage(options);
            return;
        }

        final boolean inPlace = cmd.hasOption("i");
        final String fileName = cmd.getOptionValue("f");
        final Converter converter = new Converter();

        try {
            converter.convert(fileName, inPlace);
        }
        catch (final IOException e) {
            System.out.println("an Error occured: " + e);
            e.printStackTrace();
        }

    }
}
