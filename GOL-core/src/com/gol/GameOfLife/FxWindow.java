package com.gol.GameOfLife;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.*;
import java.util.HashSet;

public class FxWindow extends Application {

    public static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    public String title = "Game of Life";
    public static boolean showGrid = false;
    public static int tileGap = 1;
    public static Dimension tileOffset = new Dimension(0, 0);

    static GOLcore core = new GOLcore();
    public RunningThread thread = new RunningThread(core);

    TilePane editPaneTiles;
    static Canvas canvas = new Canvas(core.size.width * core.tileSize.width, core.size.height * core.tileSize.height);

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle(title);

        //Manuels Branch
        //Ui elements
        Button mainEditButton = new Button("Edit");
        Button mainConfirmButton = new Button("Confirm");

        Button mainNextGenButton = new Button("Next Generation");
        Button mainRunButton = new Button("Run");
        mainRunButton.managedProperty().bind(mainRunButton.visibleProperty());
        Button mainStopButton = new Button("Stop");
        mainStopButton.managedProperty().bind(mainRunButton.visibleProperty());
        mainStopButton.setDisable(true);
        Button mainSpeedConfirmButton = new Button("Enter");

        HBox mainRunBox = new HBox();
        mainRunBox.getChildren().addAll(mainRunButton, mainStopButton);

        Slider mainSimSpeedSlider = new Slider(core.minSimSpeed, core.maxSimSpeed, core.simSpeed);
        mainSimSpeedSlider.setShowTickLabels(true);
        mainSimSpeedSlider.setShowTickMarks(true);
        mainSimSpeedSlider.setMajorTickUnit((double) core.simSpeed / 2);
        mainSimSpeedSlider.setMinorTickCount(core.simSpeed / 20);
        mainSimSpeedSlider.setBlockIncrement((double) core.simSpeed / 10);

        Button editSaveButton = new Button("Save");
        Button editEnterButton = new Button("Enter");
        Button editClearButton = new Button("Clear");

        Slider tileWidthSlider = new Slider(0, core.tileSize.width, core.tileSize.width);
        Slider tileHeightSlider = new Slider(0, core.tileSize.height, core.tileSize.height);

        TextField mainTileGapField = new TextField(Integer.toString(tileGap));

        Label widthLabel = new Label("width:");
        widthLabel.setAlignment(Pos.CENTER);
        TextField widthField = new TextField(Integer.toString(core.size.width));
        widthField.setAlignment(Pos.CENTER);
        widthField.setMaxWidth(50);
        HBox widthBox = new HBox();
        widthBox.getChildren().addAll(widthLabel, widthField);
        widthBox.setSpacing(5);
        widthBox.setAlignment(Pos.CENTER);

        Label heightLabel = new Label("height:");
        widthLabel.setAlignment(Pos.CENTER);
        TextField heightField = new TextField(Integer.toString(core.size.height));
        heightField.setAlignment(Pos.CENTER);
        heightField.setMaxWidth(50);
        HBox heightBox = new HBox();
        heightBox.getChildren().addAll(heightLabel, heightField);
        heightBox.setSpacing(5);
        heightBox.setAlignment(Pos.CENTER);

        VBox mainRightUI = new VBox(); //TODO presets
        mainRightUI.setPadding(new Insets(10, 10, 10, 10));
        mainRightUI.setSpacing(10);
        mainRightUI.getChildren().addAll(mainNextGenButton, mainSimSpeedSlider, mainSpeedConfirmButton, mainRunBox);

        HBox mainBottomUI = new HBox();
        mainBottomUI.setPadding(new Insets(20, 20, 20, 20));
        mainBottomUI.setSpacing(10);
        mainBottomUI.setAlignment(Pos.CENTER);
        mainBottomUI.getChildren().addAll(mainEditButton, tileWidthSlider, tileHeightSlider, mainTileGapField, mainConfirmButton);

        HBox editBottomUI = new HBox();
        editBottomUI.setPadding(new Insets(20, 20, 20, 20));
        editBottomUI.setSpacing(10);
        editBottomUI.setAlignment(Pos.CENTER);
        editBottomUI.getChildren().addAll(editSaveButton, widthBox, heightBox, editEnterButton, editClearButton);

        //Def main panel
        BorderPane mainBorder = new BorderPane();
        //Canvas canvas = new Canvas(core.size.width * core.tileSize.width, core.size.height * core.tileSize.height);
        StackPane background = new StackPane(canvas);
        background.setStyle("-fx-background-color: BLACK");
        background.setPadding(new Insets(20, 20, 20, 20));
        mainBorder.setCenter(background);
        mainBorder.setBottom(mainBottomUI);
        mainBorder.setRight(mainRightUI);

        refreshMainTiles();

        Scene main = new Scene(mainBorder);

        //Def edit panel
        refreshEditTiles();
        editPaneTiles.setAlignment(Pos.CENTER);
        editPaneTiles.setPrefColumns(core.size.width);
        editPaneTiles.setPrefRows(core.size.height);
        editPaneTiles.setMaxWidth(Region.USE_PREF_SIZE);
        editPaneTiles.setPadding(new Insets(20, 20, 0, 20));

        BorderPane editBorder = new BorderPane();
        editBorder.setCenter(editPaneTiles);
        editBorder.setBottom(editBottomUI);

        Scene edit = new Scene(editBorder);
        edit.getStylesheets().add(String.valueOf(this.getClass().getResource("/style.css")));

        //Choose scene
        stage.setScene(main);
        stage.setResizable(false);
        stage.show();

        //Initiate run thread
        thread.start();

        //Button actions
        editEnterButton.setOnAction(e -> { //Enter button in edit panel, confirms changes to grid size, deletes current state
            int prevWidth = core.size.width;
            int prevHeight = core.size.height;

            core.size.width = parseInt(widthField);
            core.size.height = parseInt(heightField);

            editPaneTiles.setPrefColumns(core.size.width);
            editPaneTiles.setPrefRows(core.size.height);

            core.state = new boolean[core.size.height][core.size.width];
            for (int i = 0; i < core.size.height; i++) {
                for (int j = 0; j < core.size.width; j++) {
                    core.state[i][j] = false;
                }
            }

            refreshEditTiles();
            widthField.setText(Integer.toString(core.size.width));
            heightField.setText(Integer.toString(core.size.height));

            stage.sizeToScene();

            if (stage.getHeight() > screenSize.height - 2 || stage.getWidth() > screenSize.width - 2) {
                Alert alert = new Alert(
                        Alert.AlertType.WARNING,
                        "Window size exceeds screen size. Keep changes?",
                        ButtonType.APPLY,
                        ButtonType.CANCEL
                );

                alert.showAndWait();

                if (alert.getResult() == ButtonType.CANCEL) {
                    core.size.width = prevWidth;
                    core.size.height = prevHeight;

                    core.state = new boolean[core.size.height][core.size.width];
                    for (int i = 0; i < core.size.height; i++) {
                        for (int j = 0; j < core.size.width; j++) {
                            core.state[i][j] = false;
                        }
                    }

                    refreshEditTiles();
                    widthField.setText(Integer.toString(core.size.width));
                    heightField.setText(Integer.toString(core.size.height));

                    editPaneTiles.setPrefColumns(core.size.width);
                    editPaneTiles.setPrefRows(core.size.height);

                    stage.sizeToScene();
                }
            }

        });

        editSaveButton.setOnAction(e -> { //Save button in edit panel, confirms entered tile config and sets scene to main panel
            for (int j = 0; j < core.size.width; j++) {
                for (int i = 0; i < core.size.height; i++) {
                    core.state[i][j] = ((CheckBox) editPaneTiles.getChildren().get(j * core.size.height + i)).isSelected();
                } //TODO wtf it dond work for non-squares
            }

            refreshMainTiles();
            stage.setScene(main);
        });

        mainEditButton.setOnAction(e -> { //Edit button in main panel, sets scene to edit panel
            refreshEditTiles();
            if (core.running) {
                core.running = false;
                mainRunBox.getChildren().get(0).setDisable(false);
                mainRunBox.getChildren().get(1).setDisable(true);
            }
            stage.setScene(edit);
        });

        mainConfirmButton.setOnAction(e -> { //Applies current slider and tile gap inputs
            core.tileSize = new Dimension((int) tileWidthSlider.getValue(), (int) tileHeightSlider.getValue());
            tileGap = parseInt(mainTileGapField);
            refreshMainTiles();
        });

        mainNextGenButton.setOnAction(e -> {
            core.state = core.nextGeneration(core.state);
            refreshMainTiles();
        });

        editClearButton.setOnAction(e -> { //TEMP only works bcs refreshEditTiles is brok, separate func needed
            refreshEditTiles();
        });

        mainRunButton.setOnAction(e -> {
            core.running = true;
            mainRunBox.getChildren().get(0).setDisable(true);
            mainRunBox.getChildren().get(1).setDisable(false);
        });

        mainStopButton.setOnAction(e -> {
            core.running = false;
            mainRunBox.getChildren().get(0).setDisable(false);
            mainRunBox.getChildren().get(1).setDisable(true);
        });

        mainSpeedConfirmButton.setOnAction(e -> {
            core.simSpeed = (int) mainSimSpeedSlider.getValue();
        });

        //Override window close request to kill thread if window closed
        stage.setOnCloseRequest(windowEvent -> {
            try {
                thread.join(1); //TODO let the glorious Thread.destroy(); method return pls
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.exit();
            System.exit(0);
        });

    }

    public int parseInt(TextField input){ //Convenience bundle; checks and returns integer from a text field
        try {
            return Integer.parseInt(input.getText());
        } catch(NumberFormatException e) {
            throw new NumberFormatException("suck dick");
        }
    }

    public void refreshEditTiles() { //Refreshes edit tiles according to current core attributes, will overwrite previous data
        //FIXME no it fucking doesnt, just clears all data, cbf to fix it rn
        if (editPaneTiles == null) {
            editPaneTiles = new TilePane();
        }

        HashSet<CheckBox> checkBoxes = new HashSet<>();
        for (int i = 0; i < core.size.height; i++) {
            for (int j = 0; j < core.size.width; j++) {
                CheckBox checkBox = new CheckBox();
                //checkBox.setSelected(core.state[i][j]);
                checkBoxes.add(checkBox);
            }
        }

        this.editPaneTiles.getChildren().clear();
        this.editPaneTiles.getChildren().addAll(checkBoxes);
    }

    public static void refreshMainTiles() { //Draws tiles to main panel according to core state
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        graphics.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        graphics.setFill(Color.WHITE);
        graphics.setStroke(Color.DARKGRAY);

        canvas.setWidth(core.size.width * core.tileSize.width);
        canvas.setHeight(core.size.height * core.tileSize.height);

        if (showGrid) {
            for (int i = 0; i <= core.size.height; i++) {
                graphics.strokeLine(0, i * core.tileSize.height, (int) canvas.getWidth(), i * core.tileSize.height);
            }

            for (int i = 0; i <= core.size.width; i++) {
                graphics.strokeLine(i * core.tileSize.width, 0, i * core.tileSize.width, (int) canvas.getHeight());
            }
        }

        for (int i = 0; i < core.size.height; i++) {
            for (int j = 0; j < core.size.width; j++) {
                if (core.state[i][j]) {
                    graphics.fillRect(
                            (i * core.tileSize.width) + tileGap + tileOffset.width,
                            (j * core.tileSize.height) + tileGap + tileOffset.height,
                            core.tileSize.width - tileGap * 2, core.tileSize.height - tileGap * 2);
                }
            }
        }
    }

}
