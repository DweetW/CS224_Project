package reporter;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GuiMain extends Application {

    private final WikipediaClient client = new WikipediaClient();
    private final RevisionParser parser = new RevisionParser();

    private TextField articleField;
    private Button searchButton;
    private ListView<String> revisionsList;
    private Label statusLabel;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Wikipedia Revision Reporter");

        // Input field
        articleField = new TextField();
        articleField.setPromptText("Enter Wikipedia article name");

        // Search button
        searchButton = new Button("Search");
        searchButton.setOnAction(e -> onSearchClicked());

        // Top bar layout
        HBox topBar = new HBox(8, new Label("Article:"), articleField, searchButton);
        topBar.setPadding(new Insets(10));

        // List of revisions
        revisionsList = new ListView<>();

        // Status label
        statusLabel = new Label("Enter an article name and click Search.");
        statusLabel.setPadding(new Insets(10));

        // Root layout
        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(revisionsList);
        root.setBottom(statusLabel);

        Scene scene = new Scene(root, 700, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void onSearchClicked() {
        String article = articleField.getText().trim();
        if (article.isEmpty()) {
            showNoArticleDialog();
            return;
        }
        runQuery(article);
    }

    private void runQuery(String article) {
        // Disable UI during query
        searchButton.setDisable(true);
        articleField.setDisable(true);
        statusLabel.setText("Querying Wikipedia for: " + article + " ...");
        revisionsList.getItems().clear();

        Task<List<Revision>> task = new Task<>() {
            @Override
            protected List<Revision> call() {
                InputStream in = client.fetchRevisions(article);
                List<Revision> revisions = parser.parse(in);

                // Sort by timestamp (descending) and limit to 15
                return revisions.stream()
                        .sorted(Comparator.comparing(Revision::getTimestamp).reversed())
                        .limit(15)
                        .collect(Collectors.toList());
            }
        };

        task.setOnSucceeded(e -> {
            List<Revision> revisions = task.getValue();

            if (revisions.isEmpty()) {
                statusLabel.setText("No revisions found for: " + article);
            } else {
                List<String> items = revisions.stream()
                        .map(r -> r.getTimestamp() + " — " + r.getUser())
                        .collect(Collectors.toList());

                revisionsList.getItems().setAll(items);
                statusLabel.setText("Showing " + revisions.size() + " most recent changes for: " + article);
            }

            enableInteraction();
        });

        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            handleFailure(ex, article);
            enableInteraction();
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void handleFailure(Throwable ex, String article) {
        if (ex instanceof PageNotFoundException) {
            revisionsList.getItems().clear();
            statusLabel.setText("No Wikipedia page found for: " + article);
        } else if (ex instanceof NetworkException) {
            showNetworkErrorDialog();
        } else {
            showUnexpectedErrorDialog(ex);
        }
    }

    private void enableInteraction() {
        searchButton.setDisable(false);
        articleField.setDisable(false);
    }

    private void showNoArticleDialog() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("No Article Name");
        alert.setHeaderText(null);
        alert.setContentText("Please enter a Wikipedia article name.");
        alert.showAndWait();
    }

    private void showNetworkErrorDialog() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Network Error");
        alert.setHeaderText("Unable to reach Wikipedia");
        alert.setContentText("Please check your network connection and try again.");
        alert.showAndWait();
    }

    private void showUnexpectedErrorDialog(Throwable ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Unexpected Error");
        alert.setHeaderText("An unexpected error occurred.");
        alert.setContentText(ex.getMessage() == null ? ex.toString() : ex.getMessage());
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

