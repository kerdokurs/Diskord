package diskord.server;

import io.netty.channel.ChannelHandlerContext;

public class ClientHandler {
  ClientHandler() {
  }

  /**
   * Siin handlime tegelikult kasutaja sõnumeid
   *
   * @param ctx kontekst
   * @param msg saadud sõnum
   */
  public static void handleMessage(final ChannelHandlerContext ctx, String msg) {
    // Kõigepealt tuleks sõnum deserialiseerida
    msg = msg.trim();

    // Siin otsustame, mida sõnumiga teha
    switch (msg) {
      case "bink":
        ctx.writeAndFlush("bonk");
        break;
      default:
        ctx.writeAndFlush(msg.toUpperCase());
    }
  }
}
