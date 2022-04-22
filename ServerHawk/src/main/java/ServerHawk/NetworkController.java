package ServerHawk;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class NetworkController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;

    // Button that takes the user to the home screen
    @FXML
    private Button homeSelect;

    // Button that takes the user to the CPU page
    @FXML
    private Button cpuSelect;

    // Button that takes the user to the GPU page
    @FXML
    private Button gpuSelect;

    // Button that takes the user to the RAM page
    @FXML
    private Button ramSelect;

    // Button that takes the user to the HDD/SDD page
    @FXML
    private Button storageSelect;

    // Button the takes the user to the network page
    @FXML
    private Button networkSelect;

    // Displays the name of the network connection
    @FXML
    private Label network_Connection_Name;

    // Displays the number of packets sent by the computer
    @FXML
    private Label network_Packets_Sent;

    // Displays the number of bytes sent by the computer over the network
    @FXML
    private Label network_Bytes_Sent;

    @FXML
    private Label network_Packets_Received;

    @FXML
    private Label network_Bytes_Received;

    @FXML
    private Label network_Mac_Address;

    @FXML
    private Label systemName;

    @FXML
    private Label network_Connection_Type;

    @FXML
    private Label network_IP4_Address;

    @FXML
    private Label network_IP6_Address;

    @FXML
    private PieChart networkPie;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hal = systemInfo.getHardware();
        OperatingSystem os = systemInfo.getOperatingSystem();

        systemName.setText("System Name: " + getSystemName(os));

        // Creates slices to populate the pie chart
        PieChart.Data slice1 = new PieChart.Data("Bytes Sent", hal.getNetworkIFs().get(0).getBytesSent());
        PieChart.Data slice2 = new PieChart.Data("Bytes Received"  , hal.getNetworkIFs().get(0).getBytesRecv());
        networkPie.getData().add(slice1);
        networkPie.getData().add(slice2);

        network_Connection_Name.setText(hal.getNetworkIFs().get(0).getDisplayName());
        network_Connection_Type.setText(hal.getNetworkIFs().get(0).getIfAlias());
        network_Mac_Address.setText(String.valueOf(hal.getNetworkIFs().get(0).getMacaddr()));
        network_IP4_Address.setText(Arrays.toString(hal.getNetworkIFs().get(0).getIPv4addr()));
        network_IP6_Address.setText(Arrays.toString(hal.getNetworkIFs().get(0).getIPv6addr()));
        network_Packets_Sent.setText(String.valueOf(hal.getNetworkIFs().get(0).getPacketsSent()));
        network_Bytes_Sent.setText(FormatUtil.formatBytes(hal.getNetworkIFs().get(0).getBytesSent()));
        network_Packets_Received.setText(String.valueOf(hal.getNetworkIFs().get(0).getPacketsRecv()));
        network_Bytes_Received.setText(FormatUtil.formatBytes(hal.getNetworkIFs().get(0).getBytesRecv()));

/*        // A timer that serves to update the uptime label every second
        Timer ramTimer = new Timer();
        ramTimer.schedule(new TimerTask() { // A timer task to update the seconds of the uptime label
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    public void run() {
                        hal.getNetworkIFs().get(0).updateAttributes();
                        network_Packets_Sent.setText(String.valueOf(hal.getNetworkIFs().get(0).getPacketsSent()));
                        network_Bytes_Sent.setText(FormatUtil.formatBytes(hal.getNetworkIFs().get(0).getBytesSent()));
                        network_Packets_Received.setText(String.valueOf(hal.getNetworkIFs().get(0).getPacketsRecv()));
                        network_Bytes_Received.setText(FormatUtil.formatBytes(hal.getNetworkIFs().get(0).getBytesRecv()));
                    }
                });
            }
        }, 1000, 1000); // Refresh every 1 second*/

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
