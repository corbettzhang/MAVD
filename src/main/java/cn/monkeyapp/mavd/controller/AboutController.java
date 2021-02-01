package cn.monkeyapp.mavd.controller;

import cn.monkeyapp.mavd.common.manage.LogManager;
import cn.monkeyapp.mavd.util.FileUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * @author Corbett Zhang
 */
public class AboutController extends AbstractController implements Initializable {

    private static final Logger LOGGER = LogManager.getLogger(AboutController.class);

    @FXML
    private Label versionLabel;
    @FXML
    private Button closeButton;

    @Override
    public Stage loadStage(Stage primaryStage, String listFxmlUrl) {
        super.loadingStage(primaryStage, listFxmlUrl, this);
        primaryStage.setOnCloseRequest(event -> {
            this.close(getClass().getName());
            this.removeStage(getClass().getName());
        });
        return primaryStage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        versionLabel.setText(FileUtils.getAppProperties(FileUtils.APP_VERSION));
    }


    @Override
    protected String stageTitle() {
        return "关于";
    }
}
