package diskord.payload;

// Tegemist on payloadi tüübiga, mis määrab ära, mida tegema peab
public enum PayloadType {
  BINK, // ping, aga meie ikooniga sobivas kirjastiilis.
  BONK,
  JOIN,
  LEAVE,

  INFO,
  INFO_SERVERS,
  INFO_CHANNELS,

  INVALID,

  AUTH_ERROR,

  MSG,
  MSG_OK,
  MSG_ERROR,

  ATTACHMENT_ERROR,

  LOGIN,
  LOGIN_OK,
  LOGIN_ERROR,

  REGISTER,
  REGISTER_OK,
  REGISTER_ERROR
}
