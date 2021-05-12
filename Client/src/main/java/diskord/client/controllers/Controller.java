package diskord.client.controllers;

import diskord.payload.Payload;
import diskord.payload.PayloadType;

import java.io.IOException;
import java.util.Set;

public interface Controller {
    void handleResponse(Payload response) throws IOException;
    Set<PayloadType> getListenTypes();
}
