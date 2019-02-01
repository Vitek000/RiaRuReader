package newscrawler;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import newscrawler.model.RiaArticle;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;


public class RiaUI extends Application {

    private static int readUnreadCounter = 0;
    private final Label lastUpdatedLbl = new Label("LastUpdated");
    private static Date lastUpdateDate;


    /**
     * count hours for update
     */
    private int periodUpdateHours = 2; // Default
    private TableView<RiaArticle> table = new TableView<>();
    //private final ObservableList<RiaArticle> data = ;
    private List<RiaArticle> allData = new ArrayList<>();

    public static boolean checkRedirects = true;


    private boolean isHideReadArticles = false;
    private boolean isUpdateOnlyByLastUpdateTime = true;

    private String currentPosition = ""; // active position / size

    private static Logger logger = Logger.getLogger(RiaUI.class);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws NoSuchFieldException, IllegalAccessException, ParseException, IOException {
        Scene scene = new Scene(new Group());
        stage.setTitle("RIARU reader!");
        stage.setWidth(1600);
        stage.setHeight(800);

        final Label titleLabel = new Label("Ria Ru News");
        titleLabel.setFont(new Font("Arial", 20));

        table.setEditable(true);

        TableColumn titleCol = new TableColumn("Title");
        titleCol.setMinWidth(1400);
        titleCol.setCellValueFactory(
                new PropertyValueFactory<RiaArticle, String>("title"));


        titleCol.setCellFactory(column -> new TableCell<RiaArticle, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                setText(empty || item == null ? "" : getItem());
                setGraphic(null);

                TableRow<RiaArticle> currentRow = getTableRow();


                if (!isEmpty() && item != null) {

                    styleProperty().bind(Bindings.when(currentRow.selectedProperty())
                            .then("-fx-font-weight: bold; -fx-font-size: 30;")
                            .otherwise(""));

                    if (currentRow.getItem() != null && (currentRow.getItem().isNew()))
//                            currentRow.setStyle("-fx-background-color:lightcoral");
                        //currentRow.setStyle("-fx-color:red");
                        currentRow.setStyle("-fx-font-weight: bold; -fx-font-size: 20;");
                        //currentRow.setStyle("-fx-font-size: 20;");
//                    else  if(currentRow.isSelected()) {
//                        logger.debug("Selected!!!");
//                        currentRow.setStyle("-fx-font-weight: bold; -fx-font-size: 20;");
//                    }
                    else
                        currentRow.setStyle("");

                }
            }
        });


        // Table date column
        TableColumn dateCol = new TableColumn("Date");
        dateCol.setMinWidth(200);
        dateCol.setCellValueFactory(
                new PropertyValueFactory<RiaArticle, String>("date"));


        TableColumn viewsCountCol = new TableColumn("Views");
        viewsCountCol.setMinWidth(100);
        viewsCountCol.setCellValueFactory(
                new PropertyValueFactory<RiaArticle, Long>("viewsCount"));

//        // Table Rownumber column
//        TableColumn numberCol = new TableColumn("#");
//        numberCol.setCellValueFactory((Callback<TableColumn.CellDataFeatures<RiaArticle, RiaArticle>, ObservableValue<RiaArticle>>)
//                p -> new ReadOnlyObjectWrapper<>(p.getValue()));
//        numberCol.setCellFactory(new Callback<TableColumn<RiaArticle, RiaArticle>, TableCell<RiaArticle, RiaArticle>>() {
//            @Override public TableCell<RiaArticle, RiaArticle> call(TableColumn<RiaArticle, RiaArticle> param) {
//                return new TableCell<RiaArticle, RiaArticle>() {
//                    @Override protected void updateItem(RiaArticle item, boolean empty) {
//                        super.updateItem(item, empty);
//
//                        if (this.getTableRow() != null && item != null) {
//                            setText((this.getTableRow().getIndex() + 1) + "");
//                        } else {
//                            setText("");
//                        }
//                    }
//                };
//            }
//        });
//        numberCol.setSortable(false);


        Collection<RiaArticle> values = RiaRuPageByPageFromArchiveUrl.getArticlesFromToGetCurrentNewsForLastHours(periodUpdateHours).values();

        ObservableList<RiaArticle> data = FXCollections.observableArrayList(
                values

        );

        allData.clear();
        allData.addAll(values);

        table.setItems(data);
//        table.getColumns().addAll(titleCol, dateCol, viewsCountCol/*, numberCol*/);
        table.getColumns().addAll(titleCol, dateCol, viewsCountCol);


        lastUpdateDate = new Date();
        lastUpdatedLbl.setText(String.valueOf(lastUpdateDate));

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));


        //table.
//                setStyle("-fx-selection-bar: red; -fx-selection-bar-non-focused: salmon;");
//        setStyle("-fx-font-weight: bold; -fx-font-size: 20;");
        //setStyle("-fx-selection-font-size: 20px;");

        // Show link in Browser!!!
        table.setRowFactory(tv -> {
            TableRow<RiaArticle> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    RiaArticle rowData = row.getItem();
                    logger.debug("DOUBLE CLICK: " + rowData);
                    getHostServices().showDocument(rowData.articleLink);
                }

            });

            return row;
        });


        // Button Refresh
        Button refreshBtn = new Button("Refresh");
        addActionOnRefreshButton(refreshBtn);

        // Button clear all
        Button clearAllBtn = new Button("Clear All");
        addActionOnClearAllButton(clearAllBtn);

        // Button read / unread
        Button makeAllReadUnReadBtn = new Button("Make all Read/UnRead");
        addActionOnMakeAllReadUnReadButton(makeAllReadUnReadBtn);


        // CheckBox hide read News
        CheckBox hideReadNewsChk = new CheckBox("Hide read news");
        addActionOnHideReadNewsChk(hideReadNewsChk);

        //
        Label positionInfoLabel = new Label("1 / 111");




        // Text for last hours update
        TextField lastHoursUpdateTextField = new TextField(periodUpdateHours + "");
        lastHoursUpdateTextField.setTooltip(new Tooltip("Deep news update in hours!"));
        lastHoursUpdateTextField.textProperty().addListener((observable, oldValue, newValue) -> {
//            logger.debug("textfield changed from " + oldValue + " to " + newValue);
            try {
                periodUpdateHours = Integer.parseInt(newValue);
            } catch (NumberFormatException e) {
                lastHoursUpdateTextField.setText(periodUpdateHours + "");
            }

        });

        // table selection listener
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                logger.debug(obs.getValue());

                int currentPosition = table.getSelectionModel().selectedIndexProperty().get() + 1;
                String positionInfo = currentPosition + " / " + table.getItems().size();
                positionInfoLabel.setText(positionInfo);

                newSelection.setSelected(true);

                if (oldSelection != null && oldSelection.isNew()) {
                    oldSelection.setNew(false);
                    oldSelection.setSelected(false);

                    //currentRow.setStyle("-fx-font-weight: bold;");



                    if (isHideReadArticles) {
//                        List<RiaArticle> currentData = new ArrayList<>(table.getItems());
//                        currentData.remove(oldSelection);
//                        table.setItems(FXCollections.observableArrayList(currentData));
                        table.getItems().remove(oldSelection);
                    }

                    table.refresh();
                }

            }
        });

        HBox hBox = new HBox();
        hBox.setSpacing(5);
        hBox.setPadding(new Insets(10, 0, 0, 10));
        hBox.getChildren().addAll(refreshBtn, clearAllBtn, makeAllReadUnReadBtn, hideReadNewsChk, lastHoursUpdateTextField, positionInfoLabel);
        //hBox.setStyle("-fx-border-color: red;");

//        table.prefHeightProperty().bind(stage.heightProperty());
        //table.prefWidthProperty().bind(stage.widthProperty());


        //scene.getRoot().setStyle("-fx-border-color: red;");

        vbox.getChildren().addAll(hBox, titleLabel, table, lastUpdatedLbl);
        //vbox.setStyle("-fx-border-color: red;");

        table.setMinHeight(800);


        ((Group) scene.getRoot()).getChildren().addAll(vbox);

        stage.setScene(scene);
        stage.show();
    }

    private void addActionOnHideReadNewsChk(CheckBox hideReadNewsChk) {
        hideReadNewsChk.setOnAction(e -> {

            logger.debug("event happened " + new Date());


            isHideReadArticles = ((CheckBox) e.getSource()).isSelected();

            logger.debug("event happened " + isHideReadArticles);

            RiaArticle testArticle = new RiaArticle();
            testArticle.setTitle("Test Article Title " + new Date());
            testArticle.setDate("Test Article Date " + new Date());

            List<RiaArticle> items = new ArrayList<>(allData);

            if (isHideReadArticles) {
                List<RiaArticle> filteredItems = items.stream().filter(RiaArticle::isNew).collect(Collectors.toList());
                table.setItems(FXCollections.observableArrayList(filteredItems));
            } else {
                table.setItems(FXCollections.observableArrayList(allData));
            }

            table.refresh();

        });


    }


    private void addActionOnRefreshButton(Button refreshBtn) {

        refreshBtn.setOnAction(e -> {
            try {

                logger.debug("event happened " + new Date());

                Map<String, RiaArticle> newArticlesFromToGetCurrentNewsForLastHour;
                if(isUpdateOnlyByLastUpdateTime && !allData.isEmpty()) {
                    long fullHoursBeforeUpdates = TimeUnit.MILLISECONDS.toHours(new Date().getTime() - lastUpdateDate.getTime());
                    if(fullHoursBeforeUpdates < periodUpdateHours) {
                        if(fullHoursBeforeUpdates == 0) fullHoursBeforeUpdates = 1;
                        newArticlesFromToGetCurrentNewsForLastHour = RiaRuPageByPageFromArchiveUrl.getArticlesFromToGetCurrentNewsForLastHours((int) fullHoursBeforeUpdates);
                    }
                    else {
                        newArticlesFromToGetCurrentNewsForLastHour = RiaRuPageByPageFromArchiveUrl.getArticlesFromToGetCurrentNewsForLastHours(periodUpdateHours);
                    }
                }
                else {
                    newArticlesFromToGetCurrentNewsForLastHour = RiaRuPageByPageFromArchiveUrl.getArticlesFromToGetCurrentNewsForLastHours(periodUpdateHours);
                }

                Map<String, RiaArticle> oldArticles = new LinkedHashMap<>();
//                ObservableList<RiaArticle> items = table.getItems();

                List<RiaArticle> items = new ArrayList<>(allData);

                for (RiaArticle item : items) {
                    item.setNew(false);
                    oldArticles.put(item.articleLink, item);
                }

                RiaRuPageByPageFromArchiveUrl.mergeArticles(oldArticles, newArticlesFromToGetCurrentNewsForLastHour);

                ObservableList<RiaArticle> newData = FXCollections.observableArrayList(

                        oldArticles.values()
                        //testArticle
                );

                newData.sort((o1, o2) -> {
                    try {
                        Date date1 = new SimpleDateFormat("yyyyMMdd HH:mm").parse(o1.getDate());
                        Date date2 = new SimpleDateFormat("yyyyMMdd HH:mm").parse(o2.getDate());
                        return date2.compareTo(date1);
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    return 0;
                });

                allData.clear();
                allData.addAll(newData);

                if (isHideReadArticles) {
                    List<RiaArticle> filteredItems = newData.stream().filter(RiaArticle::isNew).collect(Collectors.toList());
                    table.setItems(FXCollections.observableArrayList(filteredItems));
                } else {
                    table.setItems(newData);
                }

                table.refresh();

                lastUpdatedLbl.setText(String.valueOf(new Date()));

            } catch (NoSuchFieldException | IllegalAccessException | ParseException | IOException e1) {
                e1.printStackTrace();
            }
        });
    }

    /*
    Clear table!
     */
    private void addActionOnClearAllButton(Button clearAllBtn) {
        clearAllBtn.setOnAction(e -> {
            table.setItems(FXCollections.observableArrayList(
                    new ArrayList<>()
            ));
            allData.clear();
            table.refresh();
        });
    }


    /**
     * Toggle Read / Unread for all articles
     *
     * @param makeAllReadUnReadBtn
     */
    private void addActionOnMakeAllReadUnReadButton(Button makeAllReadUnReadBtn) {

        makeAllReadUnReadBtn.setOnAction(e -> {
            readUnreadCounter++;
            ObservableList<RiaArticle> items = table.getItems();
            for (RiaArticle item : items) {
                item.setNew(readUnreadCounter % 2 == 0);
            }
            table.refresh();
        });
    }


}
