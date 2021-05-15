package diskord.client.controllers;

import diskord.client.ServerConnection;
import diskord.payload.Payload;
import diskord.payload.PayloadType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Set;

public interface Controller {
    /**
     * Method that handles server response. When client sends payload to the server, it will
     * keep track of payload UUID and from what controller the payload is sent. When server
     * responds with payload, the response UUID is used to find the controller that made
     * that payload and handleResponse is called with the servers response payload
     * @param response Payload that server sent
     * @throws IOException
     */
    void handleResponse(Payload response) throws IOException;

    /**
     * Method that gets controllers supported listen types.
     * Usually when client sends server payload, client will create UUID and remember
     * from what controller did the request come from so when server responds, it can
     * use the UUID to find the correct controller.
     * But when server sends payload without the UUID, it will filter controllers
     * that have subscribed to listen with those payload types and handle the
     * payload on those controllers.
     * @return Set of supported payload types
     */
    Set<PayloadType> getListenTypes();

    /**
     * Method that sets serversConnection class. It is needed so client can communicate with
     * server and vice versa
     * @param serverConnection
     */
    void setServerConnection(ServerConnection serverConnection);

    /**
     * Method to set Main stage. It is needed when opening new stage and making javaFX
     * focus the new stage
     * @param mainStage
     */
    void setMainStage(Stage mainStage);

    /**
     * Method to set parent controller. It is needed when one controller needs to access
     * parent controllers elements
     * @param controller Parent controller
     */
    void setParentController(Controller controller);
}
