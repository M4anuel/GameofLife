package com.gol.GameOfLife;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.input.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FxWindow extends Application {

    /**
     * This class creates a GOLcore instance, and using several parameters defined below which only concern graphic
     * display it visualises the current tile state. Features include an edit pane, color scheme
     * selector, general UI among other features which allow for more simple human interaction and QOL-features.
     *
     * As of now, it also includes the node listeners. Whether they preload all functions into some buffer or the
     * functions are called each time an event is detected will determine if a separate class will be created for them
     * or not.
     *
     * This class currently acts as a core builder. This functionality will subside eventually when it is clear how the
     * JavaFx application framework functions, and how to properly interface with it from outside.
     */

    //Attributes only concerning graphic interface
    public static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    public String title = "Game of Life";
    public static Dimension tileSize = new Dimension(17, 17);
    public static Dimension tileOffset = new Dimension(0, 0);
    //presets for editable options
    public static ColorScheme colorScheme = ColorScheme.LIGHT;
    public static boolean showGrid = true;
    public static int tileGap = 1;

    public static GOLcore core = new GOLcore(true);

    public TilePane editPaneTiles;
    public static Canvas canvas = new Canvas(core.size.width * tileSize.width, core.size.height * tileSize.height);

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle(title);

        //Ui elements
        //change scene button
        Button mainEditButton = new Button("Edit");

        //right-side buttons
        Button mainNextGenButton = new Button("Next Generation");
        Button mainRunButton = new Button("Run");
        mainRunButton.managedProperty().bind(mainRunButton.visibleProperty());
        Button mainStopButton = new Button("Stop");
        mainStopButton.managedProperty().bind(mainRunButton.visibleProperty());
        mainStopButton.setDisable(true);

        //bottom buttons
        Button mainTileGapSizePlus = new Button("+");
        mainTileGapSizePlus.setFont(javafx.scene.text.Font.font("Consolas", FontWeight.THIN, 15));
        Button mainTileGapSizeMinus = new Button("-");
        mainTileGapSizeMinus.setFont(javafx.scene.text.Font.font("Consolas", FontWeight.THIN, 15));
        Label tileGapSizeLabel = new Label("Cell-size: ");
        tileGapSizeLabel.setStyle("-fx-color: " + ColorScheme.getColorName(colorScheme.menuFontColor));
        tileGapSizeLabel.setAlignment(Pos.CENTER);
        tileGapSizeLabel.setPadding(new Insets(4, 2, 0, 2));

        HBox tileSizeBox = new HBox();
        tileSizeBox.getChildren().addAll(tileGapSizeLabel, mainTileGapSizeMinus, mainTileGapSizePlus);
        tileSizeBox.setSpacing(5);


        HBox mainRunBox = new HBox();
        mainRunBox.getChildren().addAll(mainRunButton, mainStopButton);

        Slider mainSimSpeedSlider = new Slider(core.minSimSpeed, core.maxSimSpeed, core.simSpeed);
        mainSimSpeedSlider.setMajorTickUnit((double) core.simSpeed / 2);
        mainSimSpeedSlider.setMinorTickCount(core.simSpeed / 20);
        mainSimSpeedSlider.setBlockIncrement((double) core.simSpeed / 10);
        Label speed = new Label("Speed: ");
        HBox simSpeedSlider = new HBox();
        simSpeedSlider.getChildren().addAll(speed, mainSimSpeedSlider);
        VBox simSpeedOptions = new VBox();
        simSpeedOptions.getChildren().addAll(mainRunBox, simSpeedSlider);
        simSpeedOptions.setSpacing(10);

        Button editSaveButton = new Button("Save");
        Button editEnterButton = new Button("Enter");
        Button editClearButton = new Button("Clear");

        Slider tileSizeSlider = new Slider(0,tileSize.width, tileSize.width);
        Label tileSizeLabel = new Label("Grid Size: ");
        HBox TileSize = new HBox();
        TileSize.getChildren().addAll(tileSizeLabel, tileSizeSlider);

        TextField mainTileGapField = new TextField(Integer.toString(tileGap));
        mainTileGapField.setPromptText("Must be integer");

        CheckBox mainGridCheckBox = new CheckBox("Show grid");
        mainGridCheckBox.setSelected(showGrid);

        ObservableList<ColorScheme> colSchemes =
                FXCollections.observableArrayList(
                        ColorScheme.LIGHT,
                        ColorScheme.DARK
                );

        ComboBox<ColorScheme> colorSchemeComboBox = new ComboBox<>(colSchemes);
        colorSchemeComboBox.setValue(colorScheme);

        Label sizeLabel = new Label("Size:");
        sizeLabel.setAlignment(Pos.CENTER);
        TextField sizeField = new TextField(Integer.toString(core.size.width));
        sizeField.setAlignment(Pos.CENTER);
        sizeField.setMaxWidth(50);
        HBox sizeBox = new HBox();
        sizeBox.getChildren().addAll(sizeLabel, sizeField);
        sizeBox.setSpacing(5);
        sizeBox.setAlignment(Pos.CENTER);

        //TODO this here with the backgrounds screams for a factory implementation *wink wink*
        VBox mainRightForeground = new VBox();
        mainRightForeground.setPadding(new Insets(10, 10, 10, 10));
        mainRightForeground.setSpacing(20);
        mainRightForeground.getChildren().addAll(mainNextGenButton, simSpeedOptions, mainGridCheckBox, colorSchemeComboBox);
        StackPane mainRightStack = new StackPane(mainRightForeground);
        mainRightStack.setStyle("-fx-background-color: " + ColorScheme.getColorName(colorScheme.menuBackground) + "; -fx-padding: 30 0 0 0; ");

        HBox mainBottomForeground = new HBox();
        mainBottomForeground.setPadding(new Insets(20, 20, 20, 20));
        mainBottomForeground.setSpacing(30);
        mainBottomForeground.setAlignment(Pos.CENTER);
        mainBottomForeground.getChildren().addAll(mainEditButton, TileSize, tileSizeBox);
        StackPane mainBottomStack = new StackPane(mainBottomForeground);
        mainBottomStack.setStyle("-fx-background-color: " + ColorScheme.getColorName(colorScheme.menuBackground));

        HBox editBottomUI = new HBox();
        editBottomUI.setPadding(new Insets(20, 20, 20, 20));
        editBottomUI.setSpacing(10);
        editBottomUI.setAlignment(Pos.CENTER);
        editBottomUI.getChildren().addAll(editSaveButton, sizeBox, editEnterButton, editClearButton);

        //Def main panel
        BorderPane mainBorder = new BorderPane();
        StackPane background = new StackPane(canvas);
        background.setStyle("-fx-background-color: " + ColorScheme.getColorName(colorScheme.canvasBackground));
        background.setPadding(new Insets(20, 20, 20, 20));
        mainBorder.setCenter(background);
        mainBorder.setBottom(mainBottomStack);
        mainBorder.setRight(mainRightStack);

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
        StackPane editStack = new StackPane(editBorder);
        editStack.setStyle("-fx-background-color: " + ColorScheme.getColorName(colorScheme.canvasBackground));

        Scene edit = new Scene(editStack);
        edit.getStylesheets().add(String.valueOf(getClass().getResource("/style.css")));

        //Choose scene
        stage.setScene(main);
        stage.setResizable(false);
        stage.show();
        //select icon
        stage.getIcons().add(new Image("data:image/png;base64,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"));

        //Button actions

        editEnterButton.setOnAction(e -> { //Enter button in edit panel, confirms changes to grid size, deletes current state
            int prevWidth = core.size.width;
            int prevHeight = core.size.height;

            core.size.width = parseInt(sizeField);
            core.size.height = parseInt(sizeField);

            editPaneTiles.setPrefColumns(core.size.width);
            editPaneTiles.setPrefRows(core.size.height);

            core.state = new boolean[core.size.height][core.size.width];
            for (int i = 0; i < core.size.height; i++) {
                for (int j = 0; j < core.size.width; j++) {
                    core.state[i][j] = false;
                }
            }

            refreshEditTiles();
            sizeField.setText(Integer.toString(core.size.width));

            stage.sizeToScene();

            if (stage.getHeight() > screenSize.height - 2 || stage.getWidth() > screenSize.width - 2) {
                Alert alert = new Alert(
                        Alert.AlertType.WARNING,
                        "Window size exceeds screen size and may cause unstable behaviour. Keep changes?",
                        ButtonType.APPLY,
                        ButtonType.CANCEL
                );

                alert.showAndWait();

                if (alert.getResult() == ButtonType.CANCEL) {
                    core.size.width = prevWidth;
                    core.size.height = prevHeight;

                    core.state = new boolean[core.size.height][core.size.width];

                    refreshEditTiles();
                    sizeField.setText(Integer.toString(core.size.width));

                    editPaneTiles.setPrefColumns(core.size.width);
                    editPaneTiles.setPrefRows(core.size.height);

                    stage.sizeToScene();
                }
            }
        });

        editSaveButton.setOnAction(e -> { //Save button in edit panel, confirms entered tile config and sets scene to main panel
            for (int j = 0; j < core.size.width; j++) {
                for (int i = 0; i < core.size.height; i++) {
                    try {
                        core.state[i][j] = ((CheckBox) editPaneTiles.getChildren().get(j * core.size.width + i)).isSelected();
                    } catch (Exception ignored) {}
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

        tileSizeSlider.valueProperty().addListener((observableValue, number, t1) -> {
            tileSize = new Dimension((int) tileSizeSlider.getValue(), (int) tileSizeSlider.getValue());
            refreshMainTiles();
        });

        mainNextGenButton.setOnAction(e -> {
            core.state = core.nextGeneration(core.state);
            refreshMainTiles();
        });

        editClearButton.setOnAction(e -> {
            for (int i = 0; i < core.size.height; i++) {
                for (int j = 0; j < core.size.width; j++) {
                    core.state[i][j] = false;
                }
            }
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

        mainTileGapSizeMinus.setOnAction(e ->{
            tileGap+=1;
            refreshMainTiles();
        });

        mainTileGapSizePlus.setOnAction(e ->{
            tileGap-=1;
            refreshMainTiles();
        });

        mainGridCheckBox.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
            showGrid = mainGridCheckBox.isSelected();
            refreshMainTiles();
        });

        mainSimSpeedSlider.valueProperty().addListener((observableValue, number, t1) -> {
            core.simSpeed = (int) mainSimSpeedSlider.getValue();
        });

        colorSchemeComboBox.valueProperty().addListener((observableValue, s, t1) -> {
            colorScheme = colorSchemeComboBox.getValue();
            //TEMP maybe add refreshMenu method, among others
            mainRightStack.setStyle("-fx-background-color: " + ColorScheme.getColorName(colorScheme.menuBackground));
            mainBottomStack.setStyle("-fx-background-color: " + ColorScheme.getColorName(colorScheme.menuBackground));
            editStack.setStyle("-fx-background-color: " + ColorScheme.getColorName(colorScheme.canvasBackground));
            background.setStyle("-fx-background-color: " + ColorScheme.getColorName(colorScheme.canvasBackground));
            mainEditButton.setStyle("-fx-background-color: " + ColorScheme.getColorName(colorScheme.menuElementColor));
            mainEditButton.setStyle("-fx-font-color:" + ColorScheme.getColorName(colorScheme.menuFontColor));

            tileGapSizeLabel.setStyle("-fx-text-fill: " + ColorScheme.getColorName(colorScheme.menuFontColor));
            tileSizeLabel.setStyle("-fx-text-fill: " + ColorScheme.getColorName(colorScheme.menuFontColor));
            sizeLabel.setStyle("-fx-text-fill: " + ColorScheme.getColorName(colorScheme.menuFontColor));
            speed.setStyle("-fx-text-fill: " + ColorScheme.getColorName(colorScheme.menuFontColor));
            mainGridCheckBox.setStyle("-fx-text-fill: " + ColorScheme.getColorName(colorScheme.menuFontColor));

            mainRightStack.setStyle("-fx-background-color: " + ColorScheme.getColorName(colorScheme.menuBackground) + "; -fx-padding: 30 0 0 0; ");
            editStack.setStyle("-fx-background-color: " + ColorScheme.getColorName(colorScheme.canvasBackground));
            refreshMainTiles();
        });

        //Override window close request to kill thread if window closed
        stage.setOnCloseRequest(windowEvent -> {
            try {
                core.thread.destroy();
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
            throw new NumberFormatException("not a number");
        }
    }

    public void refreshEditTiles() { //Refreshes edit tiles according to current core attributes
        //TODO again, do not create an entirely new panel, reuse old one instead
        if (editPaneTiles == null) {
            editPaneTiles = new TilePane();
        }

        List<CheckBox> checkBoxes = new ArrayList<>();
        ArrayList<Boolean> corelist = array_convert_toList(core.state);
        int pos = 0;

        for (int i = 0; i < core.size.height; i++) {
            for (int j = 0; j < core.size.width; j++) {
                CheckBox checkBox = new CheckBox();
                checkBox.setSelected(corelist.get(pos));
                pos+= 1;
                checkBoxes.add(checkBox);
            }
        }

        editPaneTiles.getChildren().clear();
        editPaneTiles.getChildren().addAll(checkBoxes);
    }

    public static void refreshMainTiles() { //Draws tiles to main panel according to core state
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        graphics.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        graphics.setFill(colorScheme.tiles);
        graphics.setStroke(colorScheme.grid);

        canvas.setWidth(core.size.width * tileSize.width);
        canvas.setHeight(core.size.height * tileSize.height);

        if (showGrid) {
            for (int i = 0; i <= core.size.height; i++) {
                graphics.strokeLine(0, i * tileSize.height, (int) canvas.getWidth(), i * tileSize.height);
            }

            for (int i = 0; i <= core.size.width; i++) {
                graphics.strokeLine(i * tileSize.width, 0, i * tileSize.width, (int) canvas.getHeight());
            }
        }

        for (int i = 0; i < core.size.height; i++) {
            for (int j = 0; j < core.size.width; j++) {
                if (core.state[i][j]) {
                    graphics.fillRect(
                            (i * tileSize.width) + tileGap + tileOffset.width,
                            (j * tileSize.height) + tileGap + tileOffset.height,
                            tileSize.width - tileGap * 2, tileSize.height - tileGap * 2);
                }
            }
        }
    }
    public static ArrayList<Boolean> array_convert_toList(boolean[][] state){ //converts 2d array to list
        ArrayList<Boolean> lst = new ArrayList<Boolean>();
        for (int i = 0; i < core.state.length; i++){
            for (int j = 0; j < core.state[i].length; j++){
                lst.add(state[j][i]);
            }
        }
        return lst;
    }

}
