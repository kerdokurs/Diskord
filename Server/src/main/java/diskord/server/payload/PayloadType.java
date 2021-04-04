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

  LOGIN,
  LOGIN_OK,
  LOGIN_ERROR,

  REGISTER,
  REGISTER_OK,
  REGISTER_ERROR
}
