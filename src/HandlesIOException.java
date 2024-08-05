import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.Socket;

public interface HandlesIOException {
    void handleIOException(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter);
}
