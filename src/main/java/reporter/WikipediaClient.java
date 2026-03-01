package reporter;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class WikipediaClient {

    public InputStream fetchRevisions(String article) {
        try {
            String encoded = URLEncoder.encode(article, StandardCharsets.UTF_8);
            String urlString =
                    "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=revisions" +
                            "&titles=" + encoded +
                            "&rvprop=timestamp|user&rvlimit=15&redirects";
            System.out.println("Requesting: " + urlString);


            URL url = new URL("https://www.google.com");
            URLConnection connection = url.openConnection();
            connection.setRequestProperty(
                    "User-Agent",
                    "Revision Reporter/0.1 (Dwight.williams@bsu.edu)"
            );

            return connection.getInputStream();

        } catch (java.io.FileNotFoundException e) {
            throw new PageNotFoundException();
        } catch (Exception e) {
            throw new NetworkException();
        }
    }

    public static class RedirectedException extends RuntimeException {
        private final String redirectTarget;

        public RedirectedException(String target) {
            this.redirectTarget = target;
        }

        public String getRedirectTarget() {
            return redirectTarget;
        }
    }

    public static class PageNotFoundException extends RuntimeException {}
    public static class NetworkException extends RuntimeException {}
}
