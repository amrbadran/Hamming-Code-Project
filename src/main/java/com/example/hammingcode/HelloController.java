package com.example.hammingcode;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class HelloController {
    @FXML
    private ToggleGroup Burst_Single;

    @FXML
    private TextField ErrorDatatxtField;

    @FXML
    private TextField ParityTransmittxtField;

    @FXML
    private TextField PercentagetxtField;

    @FXML
    private TextField RecivedParitytxtField;

    @FXML
    private TextField ReciviedDatatxtField;

    @FXML
    private TextField ResulttxtField;

    @FXML
    private TextField SendDatatxtField;

    @FXML
    private Label TransmitDatatxtField;

    @FXML
    private TextField TransmittedtxtField;

    @FXML
    private TextField filePathtxtField;

    @FXML
    private RadioButton radioBurst;

    @FXML
    private RadioButton radioSingle;

    @FXML
    private TextField totalCorrecttxtField;

    @FXML
    private TextField totalErrostxtField;
    Hamming obj = new Hamming();
    FileChooser fileChooser = new FileChooser();
    public void SelectFile(){

        File file = fileChooser.showOpenDialog(new Stage());
        filePathtxtField.setText(file.getPath());
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            obj.Extension = fileName.substring(dotIndex + 1);
        }

        obj.filePath = filePathtxtField.getText();

    }

    public void SendData(){
        obj.total_errors = 0;
        obj.total_correcting = 0;
        String Data = SendDatatxtField.getText();
        if(Data.isEmpty()){

            Alert msg = new Alert(Alert.AlertType.ERROR,"The Data can't be empty !!");
            msg.showAndWait();
            return;
        }
        boolean flag = true;
        for(int i =0 ; i<Data.length();i++){
            if(Data.charAt(i) != '0' && Data.charAt(i) != '1'){
                flag = false;
                break;
            }
        }
        if(flag == false) {
            Alert msg = new Alert(Alert.AlertType.ERROR,"The Data must be zero and one only !!");
            msg.showAndWait();
            return;
        }
        TransmittedtxtField.setText(obj.transmitData(Data));
        ParityTransmittxtField.setText(obj.ParitySender);

        String transmit = TransmittedtxtField.getText();
        if(radioBurst.isSelected()){
            String tmp = obj.AddBurstErrors(TransmittedtxtField.getText());
            ErrorDatatxtField.setText(tmp);
            transmit = ErrorDatatxtField.getText();
        }
        else if(radioSingle.isSelected()){
            String tmp = obj.AddError(TransmittedtxtField.getText());
            ErrorDatatxtField.setText(tmp);
            transmit = ErrorDatatxtField.getText();
        }

        ReciviedDatatxtField.setText(transmit);
        String Received = obj.ReceiveData(transmit);
        RecivedParitytxtField.setText(obj.ParityReceive);

        obj.CountErrors(TransmittedtxtField.getText(),transmit);
        totalErrostxtField.setText(String.valueOf(obj.total_errors));
        Integer Position = Integer.parseInt(RecivedParitytxtField.getText(),2);
        if(Position < (obj.n+1) && Position>0 ){
            ResulttxtField.setText("Error at Position (" + Position+")");
        }
        else if(Position == 0){
            ResulttxtField.setText("No Error Detected ");
        }
        else {
            ResulttxtField.setText("Undetected Error !!");
        }
        totalCorrecttxtField.setText(String.valueOf(obj.total_correcting));
        double percent = (double)obj.total_correcting / obj.total_errors;
        PercentagetxtField.setText(String.valueOf(percent));
    }

    public void LoadFile(){
        SendDatatxtField.setText("");
        ParityTransmittxtField.setText("");
        TransmittedtxtField.setText("");
        ErrorDatatxtField.setText("");
        ResulttxtField.setText("");
        RecivedParitytxtField.setText("");
        ReciviedDatatxtField.setText("");
        obj.total_errors = 0;
        obj.total_correcting = 0;
        if(filePathtxtField.getText().isEmpty()){
            Alert msg = new Alert(Alert.AlertType.ERROR,"The File Path can't be empty !!");
            msg.showAndWait();
            return;
        }
        try{
            obj.HammingCode();
            Alert msg = new Alert(Alert.AlertType.INFORMATION,"The File Has Been Saved as "+obj.outputPath+obj.Extension);
            msg.showAndWait();
            totalCorrecttxtField.setText(String.valueOf(obj.total_correcting));
            totalErrostxtField.setText(String.valueOf(obj.total_errors));
            double percent = (double)obj.total_correcting / obj.total_errors;
            PercentagetxtField.setText(String.valueOf(percent));
        }
        catch (Exception e){
            Alert msg = new Alert(Alert.AlertType.ERROR,"SomeThing Wrong");
            msg.showAndWait();
        }


    }
}