package diskord.payload;

// Tegemist on payloadi tüübiga, mis määrab ära, mida tegema peab
public enum PayloadType {
  BINK, // ping, aga meie ikooniga sobivas kirjastiilis.
  BONK,
  MSG,
  // JOIN_CHANNEL
  JOIN_CHANNEL,
  LEAVE,

  INFO,
  // INFO_SERVERS body properties
  // Key: "servers" value: (List<Server>) servers
  INFO_SERVERS,

  // Request to server
  // INFO_SERVER_USER_PRIVILEGE body properties
  // nothing
  // Response from server
  // INFO_SERVER_USER_PRIVILEGE body properties
  // Key: "servers" value: (List<UUID>) list of user privileged servers
  INFO_USER_PRIVILEGED_SERVERS,

  // Request to server
  //INFO_CHANNELS body properties
  // Key: "serverUUID" value: (UUID) Server UUID
  // Response from server
  //INFO_CHANNELS body properties
  // Key: "channels" value: (List<Channel>) channels
  INFO_CHANNELS,
  // Request to server
  // INFO_USER_ICONS_IN_SERVER body properties
  // Key: "uuid" value: (UUID) servers uuid
  // Key: "savedIcons" value: (UUID) User uuid array whose icons are saved already
  // Response from server
  // INFO_USER_ICONS_IN_SERVER body properties
  // Key: "iconsUuid" value: (ChatFile[]) user icons.
  INFO_USER_ICONS_IN_SERVER,
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
  // Key: "icon" vlaue: (String) base64 icon
  REGISTER,
  // REGISTER_OK body properties
  // Key: "Message" value: (String) error message
  REGISTER_OK,
  // REGISTER_ERROR body properties
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
