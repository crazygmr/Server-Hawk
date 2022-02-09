package ServerHawk;

//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class ServerHawkController implements Initializable {
    // initialize the required objects to enable the switching between scenes
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private Label bootTime;

    @FXML
    private Button cpuSelect;

    @FXML
    private Button gpuSelect;

    @FXML
    private Label hardwareInfo;

    @FXML
    private Button homeSelect;

    @FXML
    private Button networkSelect;

    @FXML
    private Label osName;

    @FXML
    private Label processorInfo;

    @FXML
    private Button ramSelect;

    @FXML
    private Button storageSelect;

    @FXML
    private Label upTime;

    @FXML
    private Label systemName;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize the appropriate objects to collect the required system information
        SystemInfo systemInfo = new SystemInfo();
        OperatingSystem os = systemInfo.getOperatingSystem();
        HardwareAbstractionLayer hal = systemInfo.getHardware();
        //ObjectMapper mapper = new ObjectMapper();

        // Sets the text to the name of the system the program is running on
        systemName.setText("System Name: " + getSystemName(os));
        // Sets the text to the date and time that the system was last booted in the format of yyyy-mm-ddThh:mm:nsnsZ
        bootTime.setText(String.valueOf(Instant.ofEpochSecond(os.getSystemBootTime())));
        // Sets the text to the uptime of the system since last boot in the format X Days, hh:mm:ss
        upTime.setText(FormatUtil.formatElapsedSecs(os.getSystemUptime()));

        // A timer that serves to update the uptime label every second
        Timer timer = new Timer();
        timer.schedule(new TimerTask() { // A timer task to update the seconds of the uptime label
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    public void run() {
                        upTime.setText(FormatUtil.formatElapsedSecs(os.getSystemUptime()));
                    }
                });
            }
        }, 1000, 1000); // Refresh every 1 second
        // Sets the text to the name of the host systems operating systems
        osName.setText(String.valueOf(os));

        processorInfo.setText(hal.getProcessor().toString());

        //
        // hardwareInfo.setText(String.valueOf(hal.getComputerSystem()));

/*        try {
            hardwareInfo.setText(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(hal.getComputerSystem()));
        } catch (JsonProcessingException e) {
            e.getMessage();
        }*/
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

    // A method that is meant to return the name of the host system that is stored in a Map Entry
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
