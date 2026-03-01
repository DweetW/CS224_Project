package reporter;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RevisionParser {

    public List<Revision> parse(InputStream stream) {
        try {
            ReadContext ctx = JsonPath.parse(stream);

            List<String> redirects = ctx.read("$.query.redirects[*].to", List.class);
            if (!redirects.isEmpty()) {
                throw new WikipediaClient.RedirectedException(redirects.get(0));
            }

            List<String> timestamps = ctx.read("$.query.pages.*.revisions[*].timestamp", List.class);
            List<String> users = ctx.read("$.query.pages.*.revisions[*].user", List.class);

            List<Revision> revisions = new ArrayList<>();
            for (int i = 0; i < timestamps.size(); i++) {
                revisions.add(new Revision(timestamps.get(i), users.get(i)));
            }

            return revisions;

        } catch (WikipediaClient.RedirectedException e) {
            throw e;
        } catch (Exception e) {
            throw new WikipediaClient.NetworkException();
        }
    }
}
