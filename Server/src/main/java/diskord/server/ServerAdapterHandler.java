package diskord.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.logging.Logger;

/**
 * See klass seadistab serveri channelis sisselugemise
 */
public class ServerAdapterHandler extends SimpleChannelInboundHandler<String> {
  /**
   * Siin handlime kanalil lugemist
   *
   * @param ctx kontekst
   * @param msg saadud sõnum
   */
  @Override
  protected void channelRead0(final ChannelHandlerContext ctx, final String msg) {
    Logger.getLogger("server").info(String.format("received: %s", msg));

    // saadame edasi eraldi handlerile
    ClientHandler.handleMessage(ctx, msg);
  }
}
