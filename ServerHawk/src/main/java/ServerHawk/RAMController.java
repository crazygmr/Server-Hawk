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
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.PhysicalMemory;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;
import oshi.util.Util;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class RAMController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private CategoryAxis Time;

    @FXML
    private LineChart<String , Number> ramUtilizationGraph;

    @FXML
    private Button cpuSelect;

    @FXML
    private Button gpuSelect;

    @FXML
    private Button homeSelect;

    @FXML
    private Button networkSelect;

    @FXML
    private Button ramSelect;

    @FXML
    private Label systemName;

    @FXML
    private Label ram_Available_Memory;

    @FXML
    private Label ram_Committed_Memory;

    @FXML
    private Label ram_Swap_Used;

    @FXML
    private Label ram_Form_Factor;

    @FXML
    private Label ram_Total_Memory;

    @FXML
    private Label ram_Frequency;

    @FXML
    private Label ram_Slots_Used;

    @FXML
    private Label ram_Swap_Total;

    @FXML
    private Label ram_Swap_In;

    @FXML
    private Label ram_Swap_Out;

    @FXML
    private Label ram_Page_Size;

    @FXML
    private Button storageSelect;

    @FXML
    private NumberAxis totalUtilization;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SystemInfo systemInfo = new SystemInfo();
        OperatingSystem os = systemInfo.getOperatingSystem();
        HardwareAbstractionLayer hal = systemInfo.getHardware();
        GlobalMemory memory = hal.getMemory();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Memory Used");
        ramUtilizationGraph.getData().add(series);
        totalUtilization.setUpperBound(memory.getTotal());

        systemName.setText(getSystemName(os));

        ram_Slots_Used.setText(String.valueOf(memory.getPhysicalMemory().size()));
        ram_Form_Factor.setText(memory.getPhysicalMemory().get(0).getMemoryType());
        ram_Frequency.setText(FormatUtil.formatHertz(memory.getPhysicalMemory().get(0).getClockSpeed()));
        ram_Committed_Memory.setText(FormatUtil.formatBytes(memory.getVirtualMemory().getVirtualInUse()) + " / " + FormatUtil.formatBytes(memory.getVirtualMemory().getVirtualMax()));
        ram_Total_Memory.setText(FormatUtil.formatBytes(memory.getTotal()));
        ram_Available_Memory.setText(FormatUtil.formatBytes(memory.getAvailable()));
        ram_Page_Size.setText(FormatUtil.formatBytes(memory.getPageSize()));
        ram_Swap_In.setText(FormatUtil.formatBytes(memory.getVirtualMemory().getSwapPagesIn()));
        ram_Swap_Out.setText(FormatUtil.formatBytes(memory.getVirtualMemory().getSwapPagesOut()));
        ram_Swap_Used.setText(FormatUtil.formatBytes(memory.getVirtualMemory().getSwapUsed()));
        ram_Swap_Total.setText(FormatUtil.formatBytes(memory.getVirtualMemory().getSwapTotal()));

        // A timer that serves to update the uptime label every second
        Timer ramTimer = new Timer();
        ramTimer.schedule(new TimerTask() { // A timer task to update the seconds of the uptime label
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    public void run() {

                        ram_Committed_Memory.setText(FormatUtil.formatBytes(memory.getVirtualMemory().getVirtualInUse()) + " / " + FormatUtil.formatBytes(memory.getVirtualMemory().getVirtualMax()));
                        ram_Available_Memory.setText(FormatUtil.formatBytes(memory.getAvailable()));
                        ram_Swap_In.setText(FormatUtil.formatBytes(memory.getVirtualMemory().getSwapPagesIn()));
                        ram_Swap_Out.setText(FormatUtil.formatBytes(memory.getVirtualMemory().getSwapPagesOut()));
                        ram_Swap_Used.setText(FormatUtil.formatBytes(memory.getVirtualMemory().getSwapUsed()));
                        ram_Swap_Total.setText(FormatUtil.formatBytes(memory.getVirtualMemory().getSwapTotal()));

                        Date currentTime = new Date();
                        series.getData().add(new XYChart.Data<>(simpleDateFormat.format(currentTime), (memory.getTotal() - memory.getAvailable())));

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
