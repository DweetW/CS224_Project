package reporter;

import org.junit.jupiter.api.Test;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RevisionParserTest {

    @Test
    void testParseReturnsList() {
        InputStream stream = getClass().getResourceAsStream("/sample.json");
        assertNotNull(stream, "sample.json not found in test/resources");
        RevisionParser parser = new RevisionParser();

        List<Revision> revisions = parser.parse(stream);

        assertNotNull(revisions);
    }
}
