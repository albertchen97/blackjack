package BlackJack;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

// NIO 服务器
public class NIOServer {
    public static void main(String[] args) {

        // 创建一个selector来管理不同的channel
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open(); Selector selector = Selector.open();) {

            // 使用LocalHost的IP地址作为服务器的链接Socket的IP地址
            InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getLocalHost(), 8000);

            // 将ServerSocketChannel与创建的InetSocketAddress绑定
            serverSocketChannel.bind(inetSocketAddress);

            // non-blocking mode
            serverSocketChannel.configureBlocking(false);

            // 将这个新绑定的 ServerSocketChannel 注册到 Selector ，说明关注点为 OP_ACCEPT（即接受新连接）
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            // 从堆空间中分配一个 1024 字节的 ByteBuffer 来缓存 channel 中传输的字节
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            // 阻塞等待就绪的 channel
            while (true) {
                // 查找是否有处于 ready 状态的 key 并将其加入 selected-key set
                if (selector.select() == 0) { // 防止虚假唤醒（spurious wakeup)
                    continue;
                }
                // 获取被选择的key set
                Set<SelectionKey> selectedKeys = selector.selectedKeys();

                // 遍历所有被选择的key
                Iterator<SelectionKey> selectionKeyIterator = selectedKeys.iterator();
                while (selectionKeyIterator.hasNext()) {
                    SelectionKey currentKey = selectionKeyIterator.next();

                    if (currentKey.isAcceptable()) {
                        // Pattern matching
                        if (currentKey.channel() instanceof ServerSocketChannel channel) {
                            SocketChannel acceptedChannel = channel.accept();
                            Socket socket = acceptedChannel.socket();
                            String clientInfo = socket.getInetAddress().getHostAddress();
                            System.out.println("CONNECTED:" + clientInfo);
                        }
                    }
                    // 用完的selected key需要被立刻移除以免旧的key被重复使用
                    selectionKeyIterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


//public class NIOServer extends Thread {
//    public void run() {
//        try (Selector selector = Selector.open(); ServerSocketChannel serverSocket = ServerSocketChannel.open();) {   // 创建Selector和Channel
//            // 绑定IP地址
//            serverSocket.bind(new InetSocketAddress(InetAddress.getLocalHost(), 8888));
//            // 将Socket的阻塞模式设置为非阻塞
//            serverSocket.configureBlocking(false);
//            // 注册到Selector，并说明关注点
//            serverSocket.register(selector, SelectionKey.OP_ACCEPT);
//            while (true) {
//                selector.select();// 阻塞等待就绪的Channel，这是关键点之一
//                Set<SelectionKey> selectedKeys = selector.selectedKeys();
//                Iterator<SelectionKey> iter = selectedKeys.iterator();
//                while (iter.hasNext()) {
//                    SelectionKey key = iter.next();
//                    // 生产系统中一般会额外进行就绪状态检查
//                    sayHelloWorld((ServerSocketChannel) key.channel());
//                    iter.remove();
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//    private void sayHelloWorld(ServerSocketChannel server) throws IOException {
//        try (SocketChannel client = server.accept();) {
//          client.write(Charset.defaultCharset().encode("Hello world!"));
//        }
//    }
//    // 省略了与前面类似的main
//}