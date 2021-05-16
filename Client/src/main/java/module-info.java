module diskord.client {
    requires  javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;
    requires lombok;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires diskord.payload;
    requires io.netty.all;
    exports diskord.client;
    // https://stackoverflow.com/questions/62659576/illegalaccessexception-cannot-access-class-c-in-module-m-because-module-m-doe
    opens diskord.client.controllers to javafx.fxml;
}