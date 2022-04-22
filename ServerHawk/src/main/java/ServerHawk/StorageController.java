package ServerHawk;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import oshi.SystemInfo;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class StorageController implements Initializable {
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

    @FXML
    private Label systemName;

    @FXML
    private Label disk_Active_Time;

    @FXML
    private Label disk_Read_Write_Speed;

    @FXML
    private Label disk_Capacity_Formatted;

    @FXML
    private Label disk_Letter;

    @FXML
    private Label disk_Make_Model;

    @FXML
    private Label disk_Number;

    @FXML
    private Label disk_Reads;

    @FXML
    private Label disk_Writes;

    @FXML
    private PieChart storagePie;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hal = systemInfo.getHardware();
        OperatingSystem os = systemInfo.getOperatingSystem();
        List<HWDiskStore> hwDiskStore = hal.getDiskStores();
        FileSystem files = os.getFileSystem();
        List<OSFileStore> fileStore = files.getFileStores();

        PieChart.Data slice1 = new PieChart.Data("Free Space", fileStore.get(0).getUsableSpace());
        PieChart.Data slice2 = new PieChart.Data("Used Space"  , fileStore.get(0).getTotalSpace() - fileStore.get(0).getUsableSpace());

        storagePie.getData().add(slice1);
        storagePie.getData().add(slice2);


        systemName.setText("System Name: " + getSystemName(os));
        disk_Make_Model.setText(hwDiskStore.get(0).getModel());
        disk_Number.setText(hwDiskStore.get(0).getPartitions().get(0).getIdentification());
        disk_Letter.setText(String.valueOf(hwDiskStore.get(0).getPartitions().get(0).getMountPoint()));
        disk_Active_Time.setText(FormatUtil.formatElapsedSecs(hwDiskStore.get(0).getTransferTime() / 1000));
        disk_Capacity_Formatted.setText(FormatUtil.formatBytes(fileStore.get(0).getUsableSpace())+ " / " + FormatUtil.formatBytes(fileStore.get(0).getTotalSpace()));
        disk_Reads.setText(FormatUtil.formatBytes(hwDiskStore.get(0).getReadBytes()));
        disk_Writes.setText(FormatUtil.formatBytes(hwDiskStore.get(0).getWriteBytes()));
        disk_Read_Write_Speed.setText((hwDiskStore.get(0).getReads()) + " / " + (hwDiskStore.get(0).getWrites()));

        // A timer that serves to update the uptime label every second
        Timer ramTimer = new Timer();
        ramTimer.schedule(new TimerTask() { // A timer task to update the seconds of the uptime label
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    public void run() {
                        hwDiskStore.get(0).updateAttributes();
                        disk_Active_Time.setText(FormatUtil.formatElapsedSecs(hwDiskStore.get(0).getTransferTime() / 1000));
                        disk_Reads.setText(FormatUtil.formatBytes(hwDiskStore.get(0).getReadBytes()));
                        disk_Writes.setText(FormatUtil.formatBytes(hwDiskStore.get(0).getWriteBytes()));
                        disk_Read_Write_Speed.setText((hwDiskStore.get(0).getReads()) + " / " + (hwDiskStore.get(0).getWrites()));

                        /*Date currentTime = new Date();
                        series.getData().add(new XYChart.Data<>(simpleDateFormat.format(currentTime), (memory.getTotal() - memory.getAvailable())));

                        if (series.getData().size() > 15) {
                            series.getData().remove(0);
                        }*/
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
