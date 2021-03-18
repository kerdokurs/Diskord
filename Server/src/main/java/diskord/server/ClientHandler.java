package diskord.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

public class ClientHandler extends ChannelInboundHandlerAdapter {
  @Override
  public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
    final ByteBuf buf = (ByteBuf) msg;

    final String received = buf.toString(CharsetUtil.UTF_8);
    System.out.printf("server received: %s%n", received);

    ctx.write(Unpooled.copiedBuffer("Hello" + received, CharsetUtil.UTF_8));
  }

  @Override
  public void channelReadComplete(final ChannelHandlerContext ctx) throws Exception {
    ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
        .addListener(ChannelFutureListener.CLOSE);
  }

  @Override
  public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
    cause.printStackTrace();
    ctx.close();
  }
}
