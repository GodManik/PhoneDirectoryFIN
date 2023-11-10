module com.telephone.phonedirectory {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;


    opens com.telephone.phonedirectory to javafx.fxml;
    exports com.telephone.phonedirectory;
}