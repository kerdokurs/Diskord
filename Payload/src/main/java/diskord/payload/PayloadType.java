package diskord.payload;

// Tegemist on payloadi tüübiga, mis määrab ära, mida tegema peab
public enum PayloadType {
  BINK, // ping, aga meie ikooniga sobivas kirjastiilis.
  BONK,
  MSG,
  JOIN,
  LEAVE,

  INFO,

  INVALID,

  AUTH_ERROR,

  CHAT_OK,
  CHAT_ERROR,
  CHAT_ERROR_EMPTY,
  CHAT_ERROR_TOOLONG,
  CHAT_ERROR_INVALID_ATTACHMENT,

  LOGIN,
  LOGIN_OK,
  LOGIN_ERROR,

  REGISTER,
  REGISTER_OK,
  REGISTER_ERROR
}
