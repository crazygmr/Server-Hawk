package ServerHawk;

import javafx.application.Platform;
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
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class GPUController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private CategoryAxis Time;

    @FXML
    private LineChart<String, Number> gpuUtilizationGraph;

    @FXML
    private Button cpuSelect;

    @FXML
    private Button gpuSelect;

    @FXML
    private Label systemName;

    @FXML
    private Label gpu_Used_Memory;

    @FXML
    private Label gpu_Current_Clock;

    @FXML
    private Label gpu_Driver_Version;

    @FXML
    private Label gpu_Make_Model;

    @FXML
    private Label gpu_Memory;

    @FXML
    private Label gpu_Base_Clock;

    @FXML
    private Label gpu_Free_Memory;

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
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hal = systemInfo.getHardware();
        OperatingSystem os = systemInfo.getOperatingSystem();
        GraphicsCard graphicsCard = hal.getGraphicsCards().get(0);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("% Utilization");
        gpuUtilizationGraph.getData().add(series);
        systemName.setText("System Name: " + getSystemName(os));

        gpu_Temperature.setText(String.format("%.1f °C", graphicsCard.getTemp()));

        gpu_Memory.setText(String.valueOf(FormatUtil.formatBytes(graphicsCard.getVRam())));

        gpu_Make_Model.setText(hal.getGraphicsCards().get(0).getName());

        gpu_Base_Clock.setText(String.format("%.2f Mhz",graphicsCard.getMaxFreq()));

        gpu_Current_Clock.setText(String.format("%.2f Mhz", graphicsCard.getCurrentFreq()));

        gpu_Driver_Version.setText(graphicsCard.getVersionInfo());

        gpu_Utilization.setText(String.format("%.1f %%", graphicsCard.getUtil()));

        gpu_Free_Memory.setText(String.format("%.1f MB", graphicsCard.getFreeMem()));

        gpu_Used_Memory.setText(String.format("%.1f MB", graphicsCard.getUsedMem()));



        // A timer that serves to update the uptime label every second
        Timer gpuTimer = new Timer();
        gpuTimer.schedule(new TimerTask() { // A timer task to update the seconds of the uptime label
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    public void run() {

                        gpu_Temperature.setText(String.format("%.1f °C", graphicsCard.getTemp()));
                        gpu_Utilization.setText(String.format("%.1f %%", graphicsCard.getUtil()));
                        gpu_Current_Clock.setText(String.format("%.2f Mhz", graphicsCard.getCurrentFreq()));
                        gpu_Free_Memory.setText(String.format("%.1f MB", graphicsCard.getFreeMem()));
                        gpu_Used_Memory.setText(String.format("%.1f MB", graphicsCard.getUsedMem()));

                        Date currentTime = new Date();
                        series.getData().add(new XYChart.Data<>(simpleDateFormat.format(currentTime), (graphicsCard.getUtil())));

                        if (series.getData().size() > 15) {
                            series.getData().remove(0);
                        }
                    }
                });
            }
        }, 1000, 1000); // Refresh every 1 second
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

    public String getSystemName(OperatingSystem os) {
        OSProcess process = os.getProcess(os.getProcessId());
        for (Map.Entry<String, String> e : process.getEnvironmentVariables().entrySet()) {
            if(e.getKey().equals("COMPUTERNAME")) {
                return e.getValue();
            }
        }
        return "DefaultValue";
    }
}
