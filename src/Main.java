import javafx.application.Application;
import javafx.collections.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.geometry.Insets;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.FileWriter;
import model.Point;
import database.DBHelper;
import java.sql.*;
import java.util.Objects;


public class Main extends Application {

    ObservableList<Point> data = FXCollections.observableArrayList();

    @Override
    public void start(Stage stage) {

        DBHelper.createTable();

        // Inputs
        Label addTitle = new Label("\uD83D\uDCCD Add New Point");
        addTitle.getStyleClass().add("form-title");

        Label idLabel = new Label("Point ID");
        Label xLabel = new Label("X Coordinate");
        Label yLabel = new Label("Y Coordinate");
        Label zLabel = new Label("Z Elevation");
        idLabel.getStyleClass().add("field-label");
        xLabel.getStyleClass().add("field-label");
        yLabel.getStyleClass().add("field-label");
        zLabel.getStyleClass().add("field-label");

        TextField idField = new TextField();
        idField.setPromptText("ID");

        TextField xField = new TextField();
        xField.setPromptText("X Coordinate");

        TextField yField = new TextField();
        yField.setPromptText("Y Coordinate");

        TextField zField = new TextField();
        zField.setPromptText("Z Coordinate");

        idField.getStyleClass().add("modern-field");
        xField.getStyleClass().add("modern-field");
        yField.getStyleClass().add("modern-field");
        zField.getStyleClass().add("modern-field");

        Button addBtn = new Button("Add Point");
        addBtn.getStyleClass().add("save-btn");
        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().add("clear-btn");
        Button distanceBtn = new Button("Distance");

        GridPane formGrid = new GridPane();
        formGrid.setHgap(15);
        formGrid.setVgap(18);

        formGrid.add(idLabel, 0, 0);
        formGrid.add(idField, 1, 0);

        formGrid.add(xLabel, 0, 1);
        formGrid.add(xField, 1, 1);

        formGrid.add(yLabel, 0, 2);
        formGrid.add(yField, 1, 2);

        formGrid.add(zLabel, 0, 3);
        formGrid.add(zField, 1, 3);
        Label resultLabel = new Label();

        // Table
        TableView<Point> table = new TableView<>(data);
        table.setPrefWidth(500);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        TableColumn<Point, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());

        TableColumn<Point, Double> xCol = new TableColumn<>("X Coordinate");
        xCol.setCellValueFactory(cellData -> cellData.getValue().xProperty().asObject());

        TableColumn<Point, Double> yCol = new TableColumn<>("Y Coordinate");
        yCol.setCellValueFactory(cellData -> cellData.getValue().yProperty().asObject());

        TableColumn<Point, Double> zCol = new TableColumn<>("Z Coordinate");
        zCol.setCellValueFactory(cellData -> cellData.getValue().zProperty().asObject());

        // Load data from DB
        table.getColumns().addAll(idCol, xCol, yCol, zCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        try (Connection conn = DBHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM points")) {

            while (rs.next()) {
                data.add(new Point(
                        rs.getInt("id"),
                        rs.getDouble("x"),
                        rs.getDouble("y"),
                        rs.getDouble("z")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Data size = " + data.size());

        ComboBox<Integer> pointBox1 = new ComboBox<>();
        ComboBox<Integer> pointBox2 = new ComboBox<>();

        for(Point p : data){
            pointBox1.getItems().add(p.getId());
            pointBox2.getItems().add(p.getId());
        }


        // STATS----------------
        Label statsTitle = new Label("📊 Elevation Statistics");
        statsTitle.getStyleClass().add("stats-title");

        //HIGHEST
        Label highTitle = new Label("Highest Elevation");
        highTitle.getStyleClass().add("card-small-title");

        Label highValue = new Label("--");
        highValue.getStyleClass().add("high-value");

        Label highPoint = new Label("Point ID: --");
        highPoint.getStyleClass().add("point-id");

        //LOWEST
        Label lowTitle = new Label("Lowest Elevation");
        lowTitle.getStyleClass().add("card-small-title");

        Label lowValue = new Label("--");
        lowValue.getStyleClass().add("low-value");

        Label lowPoint = new Label("Point ID: --");
        lowPoint.getStyleClass().add("point-id");

        //AVERAGE
        Label avgTitle = new Label("Average Elevation");
        avgTitle.getStyleClass().add("card-small-title");

        Label avgValue = new Label("--");
        avgValue.getStyleClass().add("avg-value");

        Label totalPoints = new Label("Total Points: 0");
        totalPoints.getStyleClass().add("point-id");

        // SMALL CARDS
        VBox highCard = new VBox(15, highTitle, highValue, highPoint);
        highCard.getStyleClass().add("mini-stat-card");

        VBox lowCard = new VBox(15, lowTitle, lowValue, lowPoint);
        lowCard.getStyleClass().add("mini-stat-card");

        VBox avgCard = new VBox(15, avgTitle, avgValue, totalPoints);
        avgCard.getStyleClass().add("mini-stat-card");

        //STATS ROW
        HBox statsRow = new HBox(20, highCard, lowCard, avgCard);

        // BUTTON
        Button showStatsBtn = new Button("📊 Show Elevation Stats");
        showStatsBtn.getStyleClass().add("green-btn");

        //MAIN STATS BOX
        VBox statsBox = new VBox(20,
                statsTitle,
                statsRow,
                showStatsBtn
        );

        statsBox.getStyleClass().add("card");
        statsBox.setPadding(new Insets(20));

        // Live Stats Function
        Runnable updateStats = () ->{
            if(data.isEmpty()){

                highValue.setText("--");
                lowValue.setText("--");
                avgValue.setText("--");

                highPoint.setText("Point ID: --");
                lowPoint.setText("Point ID: --");

                totalPoints.setText("Total Points: 0");

                return;
            }
            Point highest = data.get(0);
            Point lowest = data.get(0);

            double sum = 0;

            for (Point p : data) {

                if (p.getZ() > highest.getZ()) {
                    highest = p;
                }

                if (p.getZ() < lowest.getZ()) {
                    lowest = p;
                }

                sum += p.getZ();
            }
            double avg = sum / data.size();

            // Update UI
            highValue.setText(String.format("%.2f", highest.getZ()));
            lowValue.setText(String.format("%.2f", lowest.getZ()));
            avgValue.setText(String.format("%.2f", avg));

            highPoint.setText("Point ID: " + highest.getId());
            lowPoint.setText("Point ID: " + lowest.getId());

            totalPoints.setText("Total Points: " + data.size());
        };
        // Button Action
        showStatsBtn.setOnAction(e->{
            updateStats.run();
            resultLabel.setText("\uD83D\uDCC8 Statistics Updated");
        });


        // Add button
        addBtn.setOnAction(e -> {
            try {
                int id = Integer.parseInt(idField.getText());
                double x = Double.parseDouble(xField.getText());
                double y = Double.parseDouble(yField.getText());
                double z = Double.parseDouble(zField.getText());

                DBHelper.insertPoint(id, x, y, z);
                data.add(new Point(id, x, y, z));
                updateStats.run();
                pointBox1.getItems().add(id);
                pointBox2.getItems().add(id);
                resultLabel.setText("✅ Point Added");

            } catch (Exception ex) {
                resultLabel.setText("Invalid Input");
            }
        });

        // Delete
        deleteBtn.setOnAction(e -> {
            Point p = table.getSelectionModel().getSelectedItem();
            if (p != null) {
                DBHelper.deletePoint(p.getId());
                data.remove(p);
                updateStats.run();
            }
        });

        // Distance
        distanceBtn.setOnAction(e -> {
            var sel = table.getSelectionModel().getSelectedItems();
            if (sel.size() == 2) {
                Point p1 = sel.get(0);
                Point p2 = sel.get(1);

                double dist = Math.sqrt(
                        Math.pow(p1.getX() - p2.getX(), 2) +
                                Math.pow(p1.getY() - p2.getY(), 2)
                );

                resultLabel.setText("Distance: " + dist);
                resultLabel.setText("Distance: " + dist);

                FadeTransition ft = new FadeTransition(Duration.millis(500), resultLabel);
                ft.setFromValue(0);
                ft.setToValue(1);
                ft.play();
            } else {
                resultLabel.setText("Select 2 points");
            }
        });
        // Dark Mode
        Button themeToggle = new Button("🌙 Dark Mode");


//        root.setLeft(sidebar);
//        form.setPrefWidth(200);
//        form.getStyleClass().add("sidebar");

        // Table container
        VBox tableBox = new VBox(table);
        VBox.setVgrow(table, Priority.ALWAYS);
        tableBox.getStyleClass().add("table-container");


        // Root layout
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");

        // Header
        Label header = new Label("Smart Survey Data Manager");
        header.getStyleClass().add("header");

        HBox topBar = new HBox(header);
        topBar.getStyleClass().add("top-bar");

        root.setTop(topBar);

        //left sidebar
        VBox sidebar = new VBox(10,
                new Label("Add New Point"),
                idField, xField, yField, zField,
                addBtn,
                new Button("Clear Fields")
        );
        sidebar.getStyleClass().add("card");

        root.setLeft(sidebar);

        // Table buttons
        Label tableTitle = new Label("Survey Points");
        tableTitle.getStyleClass().add("section-title");

        Button updateButton = new Button("Edit Selected");
        Button deleteSelectedBtn = new Button("Delete Selected");
        HBox tableButtons = new HBox(10,
                updateButton,
                deleteSelectedBtn,
                themeToggle
        );

        VBox tableSection = new VBox(10, tableTitle, table, tableButtons);
        tableSection.getStyleClass().add("card");

        root.setCenter(tableSection);


        // Calculate Section
        Label calcTitle = new Label("Calculations");
        calcTitle.getStyleClass().add("section-title");

        Label distanceTitle = new Label("Distance between two points");
        distanceTitle.getStyleClass().add("sub-title");

        pointBox1.setPromptText("Select First Point");
        pointBox2.setPromptText("Select Second Point");

        Label distanceHeading = new Label("Distance");
        distanceHeading.getStyleClass().add("distance-heading");

        Label distanceValue = new Label("--units");
        distanceValue.getStyleClass().add("distance-value");

        VBox resultCard = new VBox(10,distanceHeading,distanceValue);
        resultCard.getStyleClass().add("result-card");

        // Calculating Button
        Button calcDistanceBtn = new Button("Calculate Distance");
        calcDistanceBtn.getStyleClass().add("purple-btn");


        // Quick Actions
        Label quickTitle = new Label("⚡ Quick Actions");
        quickTitle.getStyleClass().add("quick-actions-title");
        Button export_Btn = new Button("\uD83D\uDCC4  Export to CSV");
        Button clearBtn = new Button("\uD83E\uDDF9  Clear All Data");
        Button aboutBtn = new Button("ⓘ  About");

        export_Btn.getStyleClass().add("quick-btn");
        clearBtn.getStyleClass().add("quick-btn");
        aboutBtn.getStyleClass().add("quick-btn");

        export_Btn.setMaxWidth(Double.MAX_VALUE);
        clearBtn.setMaxWidth(Double.MAX_VALUE);
        aboutBtn.setMaxWidth(Double.MAX_VALUE);


        VBox quickBox = new VBox(20,
                new Label("Quick Actions"),
                export_Btn,
                clearBtn,
                aboutBtn
        );
        quickBox.getStyleClass().add("card");
        quickBox.setPadding(new Insets(20));
        quickBox.setPrefWidth(300);

        //Calculation Box
        HBox combosRow = new HBox(15,pointBox1,pointBox2);
        VBox calcBox = new VBox(
                15,
                calcTitle,
                distanceTitle,
                combosRow,
                calcDistanceBtn,
                resultCard
        );
        calcBox.getStyleClass().add("card");
        calcBox.setPadding(new Insets(15));


        HBox bottom = new HBox(20,calcBox, statsBox, quickBox);
        bottom.setPadding(new Insets(10));

        root.setBottom(bottom);



        Scene scene = new Scene(root, 1100, 700);
        scene.getStylesheets().add(
                getClass().getResource("style.css").toExternalForm()
        );

        stage.setTitle("Smart Survey Manager");
        stage.setScene(scene);
        stage.show();

        final boolean[] isDark = {false};

        themeToggle.setOnAction(e -> {
            if (!isDark[0]) {
                scene.getStylesheets().clear();
                scene.getStylesheets().add(
                        getClass().getResource("dark.css").toExternalForm()
                );
                themeToggle.setText("☀ Light Mode");
            } else {
                scene.getStylesheets().clear();
                scene.getStylesheets().add(
                        Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm()
                );
                themeToggle.setText("🌙 Dark Mode");
            }
            isDark[0] = !isDark[0];
        });

        // Export CSV logic
        export_Btn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save CSV File");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("CSV Files", "*.csv")
            );

            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                try (FileWriter writer = new FileWriter(file)) {

                    // Header
                    writer.write("ID,X,Y,Z\n");

                    // Data
                    for (Point p : data) {
                        writer.write(p.getId() + "," +
                                p.getX() + "," +
                                p.getY() + "," +
                                p.getZ() + "\n");
                    }

                    resultLabel.setText("✅ Exported successfully!");

                } catch (Exception ex) {
                    resultLabel.setText("❌ Export failed");
                    ex.printStackTrace();
                }
            }
        });
        // Clear All Data Button Logic
        clearBtn.setOnAction(e ->{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm");
            alert.setHeaderText("Delete All Data?");
            alert.setContentText("This action cannot be undone.");

            if(alert.showAndWait().get() == ButtonType.OK){
                DBHelper.clearAll();
                data.clear();
                resultLabel.setText("All Data Cleared.");
            }

        });

        // Edit the selected row
        table.getSelectionModel().selectedItemProperty().addListener((obs,oldval,p) ->{
            if(p!= null){
                idField.setText(String.valueOf(p.getId()));
                xField.setText(String.valueOf(p.getX()));
                yField.setText(String.valueOf(p.getY()));
                zField.setText(String.valueOf(p.getZ()));
            }
        });
        updateButton.setOnAction(e ->{
            Point selected = table.getSelectionModel().getSelectedItem();

            if(selected != null){
                try{
                int id = Integer.parseInt(idField.getText());
                double x = Double.parseDouble(xField.getText());
                double y = Double.parseDouble(yField.getText());
                double z = Double.parseDouble(zField.getText());

                DBHelper.updatePoint(id,x,y,z);

                selected.setX(x);
                selected.setY(y);
                selected.setZ(z);

                table.refresh();
                resultLabel.setText("Updated Successfully");

                }catch(Exception ex) {
                    resultLabel.setText("Invalid Input");
                }
            }else{
                resultLabel.setText("Select a row first.");
            }
        });

        //About button functionality
        aboutBtn.setOnAction(e ->{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("About");
            alert.setHeaderText("Smart Survey Manager");

            alert.setContentText(
                    "A modern surveying data management system built using JavaFX and SQLite.\n\n" +
                            "Features:\n" +
                            "• Add & manage survey points\n" +
                            "• Distance calculations\n" +
                            "• CSV export\n" +
                            "• Live statistics dashboard\n" +
                            "• Dark mode support\n\n" +
                            "Developed by Dolly ✨"
            );
            alert.showAndWait();
        });

        // logic for delete selected button
        deleteSelectedBtn.setOnAction(e ->{

            ObservableList<Point> selectedPoints =
                    table.getSelectionModel().getSelectedItems();

            if(selectedPoints != null){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete Point");
                alert.setHeaderText("Delete Selected Point?");
                alert.setContentText("You are about to delete "
                        + selectedPoints.size()
                        + " point(s).");

                if (alert.showAndWait().get() == ButtonType.OK) {

                    // copy list to avoid concurrent modification
                    ObservableList<Point> copy =
                            FXCollections.observableArrayList(selectedPoints);

                    for (Point p : copy) {
                        DBHelper.deletePoint(p.getId());
                        data.remove(p);
                        updateStats.run();
                    }

                    resultLabel.setText(
                            "🗑 Deleted " + copy.size() + " point(s)"
                    );
                }
            }
            else{
                resultLabel.setText("Select a point first");
            }
        });

        // Calculation Logic
        calcDistanceBtn.setOnAction(e -> {

            Integer id1 = pointBox1.getValue();
            Integer id2 = pointBox2.getValue();

            if (id1 != null && id2 != null) {
                Point p1 = null;
                Point p2 = null;

                for (Point p : data) {
                    if (p.getId() == id1) {
                        p1 = p;
                    }
                    if (p.getId() == id2) {
                        p2 = p;
                    }
                }
                if (p1 != null && p2 != null) {

                    double dist = Math.sqrt(
                            Math.pow(p1.getX() - p2.getX(), 2) +
                                    Math.pow(p1.getY() - p2.getY(), 2)
                    );
                    distanceValue.setText(
                            String.format("%.2f units", dist)
                    );

                }
            } else {
                resultLabel.setText("Select both points");
            }
        });

    }

    public static void main(String[] args) {
        launch();
    }
}