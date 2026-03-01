package reporter;

import java.io.InputStream;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("No article name provided.");
            return;
        }

        String article = args[0];

        try {
            WikipediaClient client = new WikipediaClient();
            InputStream stream = client.fetchRevisions(article);

            RevisionParser parser = new RevisionParser();
            List<Revision> revisions = parser.parse(stream);

            RevisionFormatter formatter = new RevisionFormatter();

            int counter = 1;
            for (Revision r : revisions) {
                System.out.println(counter + "  " + formatter.format(r));
                counter++;
            }

        } catch (WikipediaClient.RedirectedException e) {
            System.out.println("Redirected to " + e.getRedirectTarget());
        } catch (WikipediaClient.PageNotFoundException e) {
            System.err.println("No Wikipedia page found.");
        } catch (WikipediaClient.NetworkException e) {
            System.err.println("Network error occurred.");
        } catch (Exception e) {
            System.err.println("Unexpected error.");
        }
    }
}

