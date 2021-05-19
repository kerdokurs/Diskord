package diskord.payload;

// This enum handles which users should receive the response payload
public enum ResponseType {
  // Send payload to all clients in the same server (room)
  TO_ALL,
  // Send payload to all clients except the sender, e.g when user joins or sends a message
  TO_ALL_EXCEPT_SELF,
  // Send payload to only one client, e.g when user sends a private message to someone
  TO_ONE,
  // Send payload to the sender, e.g when there is an error with something
  TO_SELF,

  TO_CHANNEL_EXCEPT_SELF,
}
