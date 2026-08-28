module com.macondo.loudbird {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.macondo.loudbird to javafx.fxml;
    exports com.macondo.loudbird;
}
