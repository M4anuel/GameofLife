package com.gol.GameOfLife;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
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
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.input.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class FxWindow extends Application {

    /**
     * This class creates a GOLcore instance, and using several parameters defined below which only concern graphic
     * display it visualises the current tile state. Features include an edit pane (partially borked atm), color scheme
     * selector, general UI among other features which allow for more simple human interaction and QOL-features.
     *
     * As of now, it also includes the node listeners. Whether they preload all functions into some buffer or the
     * functions are called each time an event is detected will determine if a separate class will be created for them
     * or not.
     *
     * This class currently acts as a core builder. This functionality will subside eventually when it is clear how the
     * JavaFx application framework functions, and how to properly interface with it from outside.
     * why i be writin this nobdy gon reed it a/w
     */

    //Attributes only concerning graphic interface
    public static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    public String title = "Game of Life";
    public static boolean showGrid = true;
    public static int tileGap = 1;
    public static Dimension tileSize = new Dimension(17, 17);
    public static Dimension tileOffset = new Dimension(0, 0);
    public static ColorScheme colorScheme = ColorScheme.LIGHT;

    public static GOLcore core = new GOLcore(true);

    public TilePane editPaneTiles;
    public static Canvas canvas = new Canvas(core.size.width * tileSize.width, core.size.height * tileSize.height);

    public PresetGenerator generator = new PresetGenerator();

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle(title);

        //Ui elements
        Button mainEditButton = new Button("Edit");

        Button mainNextGenButton = new Button("Next Generation");
        Button mainRunButton = new Button("Run");
        mainRunButton.managedProperty().bind(mainRunButton.visibleProperty());
        Button mainStopButton = new Button("Stop");
        mainStopButton.managedProperty().bind(mainRunButton.visibleProperty());
        mainStopButton.setDisable(true);

        Button mainTileGapSizePlus = new Button("+");
        Button mainTileGapSizeMinus = new Button("-");
        Label tileGapSizeLabel = new Label(Integer.toString(tileGap));
        tileGapSizeLabel.setAlignment(Pos.CENTER);
        tileGapSizeLabel.setPadding(new Insets(4, 2, 0, 2));

        HBox TileGapBox = new HBox();
        TileGapBox.getChildren().addAll(mainTileGapSizeMinus, tileGapSizeLabel, mainTileGapSizePlus);
        TileGapBox.setSpacing(5);


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

        Slider tileSizeSlider = new Slider(0,tileSize.width, tileSize.width);

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

        ArrayList<Preset> presets = generator.deserialisePresets();

        ComboBox<String> presetBox = new ComboBox<>();
        presetBox.setPromptText("Preset");

        for (Preset preset : presets) {
            presetBox.getItems().add(preset.name);
        }

        //TODO this here with the backgrounds screams for a factory implementation *wink wink*
        VBox mainRightForeground = new VBox();
        mainRightForeground.setPadding(new Insets(10, 10, 10, 10));
        mainRightForeground.setSpacing(10);
        mainRightForeground.getChildren().addAll(mainNextGenButton, mainSimSpeedSlider, mainRunBox, mainGridCheckBox, colorSchemeComboBox, presetBox);
        StackPane mainRightStack = new StackPane(mainRightForeground);
        mainRightStack.setStyle("-fx-background-color: " + ColorScheme.getColorName(colorScheme.menuBackground));

        HBox mainBottomForeground = new HBox();
        mainBottomForeground.setPadding(new Insets(20, 20, 20, 20));
        mainBottomForeground.setSpacing(10);
        mainBottomForeground.setAlignment(Pos.CENTER);
        mainBottomForeground.getChildren().addAll(mainEditButton, tileSizeSlider, TileGapBox);
        StackPane mainBottomStack = new StackPane(mainBottomForeground);
        mainBottomStack.setStyle("-fx-background-color: " + ColorScheme.getColorName(colorScheme.menuBackground));

        HBox editBottomUI = new HBox();
        editBottomUI.setPadding(new Insets(20, 20, 20, 20));
        editBottomUI.setSpacing(10);
        editBottomUI.setAlignment(Pos.CENTER);
        editBottomUI.getChildren().addAll(editSaveButton, widthBox, heightBox, editEnterButton, editClearButton);

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

        //TEMP manuel shit wtf is this
        main.setOnKeyPressed(new EventHandler<KeyEvent>() { //in which scene the keylistener should be
            @Override
            public void handle(KeyEvent event) { //normal eventhandler

                if(event.getCode() == KeyCode.S){ //.getCode() is the key currently being pressed
                    int prevWidth = core.size.width;
                    int prevHeight = core.size.height;
                }
                System.out.println(event.getCode()); //just prints out what key is being pressed

            }
        });

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
        //TEMP move icon set method
        //stage.getIcons().add(new Image("data:image/jpeg;base64,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"));
        stage.getIcons().add(new Image("data:image/png;base64,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"));

        //Button actions

        //editEnterButton.setOnKeyPressed();
        editEnterButton.setOnAction(e -> { //Enter button in edit panel, confirms changes to grid size, deletes current state
            //TODO instead of recreating the entire tilepane everytime, perhaps just change existing one and then show, but dat conversion shit nefarious
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

        mainTileGapSizeMinus.setOnAction(e ->{
            tileGap+=1;
            tileGapSizeLabel.setText(Integer.toString(tileGap));
            refreshMainTiles();
        });

        mainTileGapSizePlus.setOnAction(e ->{
            tileGap-=1;
            tileGapSizeLabel.setText(Integer.toString(tileGap));
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
            refreshMainTiles();
        });

        main.setOnKeyPressed(new EventHandler<KeyEvent>() { //FIXME Space wird noni entdeckt, mues mi aber chli me ufs geo konzentriere
            //PROPOSE chönnts sy wüu space wines "enter" oder "select" zeut, aso we zb e button blau umrandet (selected) isch
            @Override
            public void handle(KeyEvent event) { //normal eventhandler

                if(event.getCode() == KeyCode.SPACE){ //.getCode() is the key currently being pressed
                    if(core.running){
                        core.running=false;
                    }
                    else {
                        core.running = false;
                    }
                }
                System.out.println(event.getCode()); //just prints out what key is being pressed

            }
        });

        presetBox.valueProperty().addListener((observableValue, s, t1) -> {
            String name = presetBox.getValue();
            for (Preset preset : presets) {
                if (preset.name.equals(name)) {
                    core.size = new Dimension(preset.minReqWindowWidth, preset.minReqWindowHeight);
                    core.state = preset.presetState;
                    editPaneTiles.setPrefColumns(core.size.width);
                    editPaneTiles.setPrefRows(core.size.height);
                    refreshMainTiles();
                    break;
                }
            }
        });

        //in which scene the keylistener should be //FIXME sött no mit dr edit scene gmacht werde, geit aber noni
        main.setOnKeyPressed(event -> { //normal eventhandler
            //TEMP meek fönctions sodass dr manuel nümme dä scheiss hie cha kabutt mache

            if(event.getCode() == KeyCode.S){ //.getCode() is the key currently being pressed
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
            }
            System.out.println(event.getCode()); //just prints out what key is being pressed
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
            throw new NumberFormatException("suck dick");
        }
    }

    public void refreshEditTiles() { //Refreshes edit tiles according to current core attributes
        //FIXME no it fucking doesnt, just clears all data, cbf to fix it rn
        //TODO again, do not create an entirely new panel, reuse old one instead
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
                    System.out.println("i brint :]");
                    graphics.fillRect(
                            (i * tileSize.width) + tileGap + tileOffset.width,
                            (j * tileSize.height) + tileGap + tileOffset.height,
                            tileSize.width - tileGap * 2, tileSize.height - tileGap * 2);
                } else {
                    System.out.println("mebi i dond x}");
                }
            }
        }

        System.out.println(Arrays.deepToString(core.state));
        System.out.print("\n");
    }

}
