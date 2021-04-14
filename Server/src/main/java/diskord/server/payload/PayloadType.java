package diskord.server.payload;

// Tegemist on payloadi tüübiga, mis määrab ära, mida tegema peab
public enum PayloadType {
  BINK, // ping, aga meie ikooniga sobivas kirjastiilis.
  BONK,
  CHAT,
  JOIN,
  LEAVE,

  INFO,

  INVALID,

  AUTH_ERROR,

  CHAT_ERROR,
  CHAT_OK,

  LOGIN,
  LOGIN_OK,
  LOGIN_ERROR,

  REGISTER,
  REGISTER_OK,
  REGISTER_ERROR
}
