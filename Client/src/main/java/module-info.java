module diskord.klient {
    requires  javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;
    exports diskord.client;
    // https://stackoverflow.com/questions/62659576/illegalaccessexception-cannot-access-class-c-in-module-m-because-module-m-doe
    opens diskord.controllers to javafx.fxml;
}