package diskord.payload;

// Tegemist on payloadi tüübiga, mis määrab ära, mida tegema peab
public enum PayloadType {
  BINK, // ping, aga meie ikooniga sobivas kirjastiilis.
  BONK,

  // MSG body properties
  // Response
  // Key: "userUuid" value: (UUID) users uuid who sent message
  // Key: "message" value: (String) user sent message
  // Optional key: "chatFile" value: (ChatFile) User sent file
  // Request
  // Key: "message" value: (String) user sent message
  // Optional key: "chatFile" value: (ChatFile) User sent file
  MSG,
  // MSG body properties
  // nothing
  MSG_OK,
  // MSG body properties
  // Key: "message" value: (String) error message
  MSG_ERROR,

  // JOIN_CHANNEL body properties
  // Key: "channel_uuid" value:(UUID) channels uuid
  JOIN_CHANNEL,
  // JOIN_CHANNEL_OK body properties
  // Key: "users" value: (User[]) Currently joined users
  JOIN_CHANNEL_OK,
  // JOIN_CHANNEL_ERROR body properties
  // Key: "message" value:(String) Error message
  JOIN_CHANNEL_ERROR,

  // LEAVE CHANNEL body properties
  // Nothing
  LEAVE_CHANNEL,
  LEAVE_CHANNEL_OK,
  // LEAVE_CHANNEL_ERROR body properties
  // nothing
  LEAVE_CHANNEL_ERROR,
  // JOIN_CHANNEL_ERROR body properties
  // Key: "message" value:(String) Error message

  // JOIN_SERVER body properties
  // Key: "joinId" value: (String) Join id that user entered
  JOIN_SERVER,
  // JOIN_SERVER_OK body properties
  // nothing
  JOIN_SERVER_OK,
  // JOIN_SERVER_ERROR body properties
  // Key: "message" value: (String) error message
  JOIN_SERVER_ERROR,

  INFO,
  // request
  // INFO_SERVERS body properties
  // nothing
  INFO_USER_SERVERS,
  // response
  // INFO_SERVERS body properties
  // Key: "joined" value: (Set<UUID>) servers
  // Key: "privileged" value: (Set<UUID>) servers
  INFO_USER_SERVERS_OK,
  // response
  // INFO_SERVERS body properties
  // Key: "message" value: (String) error message
  INFO_USER_SERVERS_ERROR,

  // Request to server
  //INFO_CHANNELS body properties
  //TODO serverUUID -> server_id
  // Key: "serverUUID" value: (UUID) Server UUID
  INFO_CHANNELS,
  // Response from server
  //INFO_CHANNELS_OK body properties
  // Key: "channels" value: (List<Channel>) channels
  INFO_CHANNELS_OK,
  // Response from server
  //INFO_CHANNELS_ERROR body properties
  // Key: "message" value: (String) error message
  INFO_CHANNELS_ERROR,

  INVALID,
  AUTH_ERROR,
  CHAT_ERROR,
  CHAT_OK,

  // LOGIN body properties
  // Key: "username" value: (String) username
  // Key: "password" value: (String) password
  LOGIN,
  // LOGIN_OK body properties
  // Key: "username" value: (String) Username

  //TODO UUID TO ID line 82

  // Key: "uuid" value: (UUID) user UUID
  // Key: "token" value: (String) auth token
  // Key: "icon" value: (String) base64icon
  LOGIN_OK,
  // LOGIN_ERROR body properties
  // Key: "Message" value: (String) error message
  LOGIN_ERROR,

  // REGISTER body properties
  // Key: "username" value: (String) username
  // Key: "password" value: (String) password
  // Key: "icon" value: (String) base64 icon
  REGISTER,
  // REGISTER_OK body properties
  // nothing
  REGISTER_OK,
  // REGISTER_ERROR body properties
  // TODO Typo Message -> message
  // Key: "Message" value: (String) error message
  REGISTER_ERROR,

  // REGISTER_SERVER body properties
  // key: "icon" value: (String) base64 icon
  // key: "name" value: (String) Servers name
  // key: "description" value: (String) Servers description
  REGISTER_SERVER,
  // REGISTER_SERVER_OK body properties
  // Nothing
  REGISTER_SERVER_OK,
  // REGISTER_SERVER_ERROR body properties
  // key: "message" value: (String) error message
  REGISTER_SERVER_ERROR,

  // REGISTER_CHANNEL body properties
  // key: "chatFile" value: (ChatFile) channel icon
  // key: "name" value: (String) channel name
  REGISTER_CHANNEL,
  // REGISTER_CHANNEL_OK body properties
  // Nothing
  REGISTER_CHANNEL_OK,
  // REGISTER_CHANNEL_ERROR body properties
  // key: "message" value: (String) error message
  REGISTER_CHANNEL_ERROR
}
