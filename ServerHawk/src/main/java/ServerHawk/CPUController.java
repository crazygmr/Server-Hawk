package ServerHawk;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
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
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;
import oshi.util.Util;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CPUController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;
    //private ScheduledExecutorService scheduledExecutorService;

    @FXML
    private CategoryAxis Time;

    @FXML
    private LineChart<String, Number> UtilizationGraph;

    @FXML
    private Button cpuSelect;

    @FXML
    private Label cpu_Base_Clock_Speed;

    @FXML
    private Label cpu_Clock_Speed;

    @FXML
    private Label cpu_Cores;

    @FXML
    private Label cpu_L1_Cache;

    @FXML
    private Label cpu_L2_Cache;

    @FXML
    private Label cpu_L3_Cache;

    @FXML
    private Label cpu_Logical_Processors;

    @FXML
    private Label cpu_Temperature;

    @FXML
    private Label cpu_Util;

    @FXML
    private Label cpu_Voltage;

    @FXML
    private Button gpuSelect;

    @FXML
    private Button homeSelect;

    @FXML
    private Button networkSelect;

    @FXML
    private Button ramSelect;

    @FXML
    private Button storageSelect;

    @FXML
    private Label systemName;

    @FXML
    private NumberAxis totalUtilization;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SystemInfo systemInfo = new SystemInfo();
        OperatingSystem os = systemInfo.getOperatingSystem();
        HardwareAbstractionLayer hal = systemInfo.getHardware();

        cpu_Util.setText(String.valueOf(hal.getProcessor().getMaxFreq()));
        systemName.setText("System Name: " + getSystemName(os));
        cpu_Base_Clock_Speed.setText(FormatUtil.formatValue(hal.getProcessor().getMaxFreq(), "hz"));

        long[] logicalProcessors = hal.getProcessor().getCurrentFreq();
        long averageFreq = Arrays.stream(logicalProcessors).sum() / logicalProcessors.length;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Data Series");
        UtilizationGraph.getData().add(series);

        cpu_Clock_Speed.setText(FormatUtil.formatValue(averageFreq, "hz"));

        cpu_Temperature.setText(String.valueOf(hal.getSensors().getCpuTemperature()));
        cpu_Voltage.setText(String.valueOf(hal.getSensors().getCpuVoltage()));

        // A timer that serves to update the uptime label every second
        Timer timer = new Timer();
        timer.schedule(new TimerTask() { // A timer task to update the seconds of the uptime label
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    public void run() {
                        //upTime.setText(FormatUtil.formatElapsedSecs(os.getSystemUptime()));
                        long[] logicalProcessors = hal.getProcessor().getCurrentFreq();
                        long averageFreq = Arrays.stream(logicalProcessors).sum() / logicalProcessors.length;

                        cpu_Clock_Speed.setText(FormatUtil.formatValue(averageFreq, "hz"));

                        Date currentTime = new Date();
                        series.getData().add(new XYChart.Data<>("Hi", 50));
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
