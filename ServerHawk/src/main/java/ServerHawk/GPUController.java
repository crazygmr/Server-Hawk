package ServerHawk;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GPUController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private CategoryAxis Time;

    @FXML
    private LineChart<?, ?> UtilizationGraph;

    @FXML
    private Button cpuSelect;

    @FXML
    private Button gpuSelect;

    @FXML
    private Label gpu_Dedicated_Memory;

    @FXML
    private Label gpu_Driver_Date;

    @FXML
    private Label gpu_Driver_Version;

    @FXML
    private Label gpu_Make_Model;

    @FXML
    private Label gpu_Memory;

    @FXML
    private Label gpu_Physical_Location;

    @FXML
    private Label gpu_Shared_Memory;

    @FXML
    private Label gpu_Temperature;

    @FXML
    private Label gpu_Utilization;

    @FXML
    private Button homeSelect;

    @FXML
    private Button networkSelect;

    @FXML
    private Button ramSelect;

    @FXML
    private Button storageSelect;

    @FXML
    private NumberAxis totalUtilization;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void switchToCPU (ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("CPU.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToGPU (ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("GPU.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void switchToNetwork(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("Network.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void switchToRAM(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("RAM.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void switchToStorage(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("HDD-SDD.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void switchToHome(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("ServerHawk.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
