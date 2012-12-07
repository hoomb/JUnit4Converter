package converter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class Converter {

    private final static String[] imports = {
        "import org.junit.After;",
        "import org.junit.Before;",
        "import org.junit.Test;",
        "import org.junit.Ignore;",
        "import static org.junit.Assert.*;"        
    };
    
    // Find & Replace Patterns
    private final static String[][] convertPatterns = {
            // Add @Test
            {
                    "^\\s*(public +void +test)",
                    "\t@Test\n    $1"
            },

            // Add @Ignore, assumed that tests are disabled by adding an underscore (_) to them,
            // change if necessary
            {
                    "^\\s*(public +void +)_(test)",
                    "\t@Test\n\t@Ignore\n    $1$2"
            },

            // Remove double @Test's on already @Test annotated files
            {
                    "^\\s*@Test\\n\\s+@Test",
                    "\t@Test"
            },

            // Remove all empty setUp's
            {
                    "^\\s*(@Override|)\\s*((public|protected)[ \\t]+)?void[ \\t]+setUp\\(\\s*\\)[^\\{]*\\{\\s*(super\\.setUp\\(\\s*\\);)?\\s*\\}(\\s*)?",
                    ""
            },

            // Remove all super.setUp calls
            {
                    "^\\s*super\\.setUp\\(\\s*\\);\\s*?",
                    ""
            },

            // Add @Before to all setUp's
            {
                    "^\\s*(@Override|)\\s*((public|protected)[ \\t]+)?(void[ \\t]+setUp\\(\\s*\\))",
                    "\t@Before\n    public void setUp()"
             },

             // Remove double @Before's on already @Before annotated files
            {
                    "^\\s+@Before\\s+@Before",
                    "\t@Before"
            },

            // Remove all empty tearDown's
            {
                    "^\\s*(@Override|)\\s*((public|protected)[ \\t]+)?void +tearDown\\(\\s*\\)[^\\{]*\\{\\s*(super\\.tearDown\\(\\s*\\);)?\\s*\\}(\\s*)?",
                    ""
            },

            // Remove all super.tearDown calls
            {
                    "^\\s+super\\.tearDown\\(\\);(\\s*\\n)?",
                    ""
            },

            // Add @After to all tearDown's
            {
                    "^\\s*(@Override|)\\s*((public|protected)[ \\t]+)?(void +tearDown\\(\\s*\\))",
                    "\t@After\n    public void tearDown()"
            },

            // Remove double @After's on already @After annotated files
            {
                    "^\\s*@After\\s*@After",
                    "\t@After"
            },

            // Remove old imports, add new imports
            {
                    "^((\\s*import\\s+junit\\.framework\\.\\*;)|(\\s*import\\s+junit\\.framework\\.Test;)|(\\s*import\\s+junit\\.framework\\.TestSuite;)|(\\s*import\\s+junit\\.framework\\.Assert;)|(\\s*import\\s+junit\\.framework\\.TestCase;))",
                    ""
            },

            // Remove all extends TestCase
            {
                    "\\s*extends\\s+TestCase\\s*",
                    " "
            },
            
            // Remove all "Assert."'s
            {
                "^\\s*Assert\\.",
                ""
        },
    };

    private String replaceString(final String inputString, final String searchString, final String replaceString) {
        final Pattern searchPattern = Pattern.compile(searchString, Pattern.MULTILINE);
        final Matcher searchMacher = searchPattern.matcher(inputString);

        final String result = searchMacher.replaceAll(replaceString);

        return result;
    }

    private String readInputFile(final String fileName) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
        final StringBuffer text = new StringBuffer();

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            text.append(line + "\n");
        }

        return text.toString();
    }

    private void writeConvertedFile(final String fileName, final String inputString) throws IOException {
        // Create file
        final FileWriter fileWriter = new FileWriter(fileName);
        final BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        bufferedWriter.write(inputString);

        // Close the output stream
        bufferedWriter.close();
    }

    protected void convert(final String fileName, final boolean writeToFile) throws IOException {
        String fileContent = readInputFile(fileName);

        //add necessary imports
        final Pattern importPattern = Pattern.compile("^\\s*import\\s+.*;", Pattern.MULTILINE);
        final Matcher importMatcher = importPattern.matcher(fileContent);
        
        int lastImportPosition = 0;
        while (importMatcher.find())
            lastImportPosition = importMatcher.end();
        
        fileContent = new StringBuffer(fileContent).insert(lastImportPosition, "\n\n" + StringUtils.join(imports, '\n')).toString();

        //main conversion
        for (final String[] convertPattern : convertPatterns) {
            final String searchPattern = convertPattern[0];
            final String replaceString = convertPattern[1];

            fileContent = replaceString(fileContent, searchPattern, replaceString);
        }

        //remove constructor
        final Pattern classNamePattern = Pattern.compile("^(.*)class\\s*([a-zA-Z0-9_]*)\\s*\\{", Pattern.MULTILINE);
        final Matcher classNameMatcher = classNamePattern.matcher(fileContent);
        
        if (classNameMatcher.find()) {
            final String className = classNameMatcher.group(2);

            fileContent = replaceString(fileContent, "^(.*)" + className + "\\s*\\(.*\\)\\s*\\{\\s*super\\s*\\(.*\\)\\s*;\\s*\\}", "");             
        }
        
        //finished! what next?
        if (writeToFile)
            writeConvertedFile(fileName, fileContent);
        else
            System.out.println(fileContent);
    }
}
