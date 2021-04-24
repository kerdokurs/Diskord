module diskord.client {
    requires  javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;
    requires lombok;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    exports diskord.client;
    // https://stackoverflow.com/questions/62659576/illegalaccessexception-cannot-access-class-c-in-module-m-because-module-m-doe
    opens diskord.client.controllers to javafx.fxml;
}