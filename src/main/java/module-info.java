module com.example.hammingcode {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.hammingcode to javafx.fxml;
    exports com.example.hammingcode;
}