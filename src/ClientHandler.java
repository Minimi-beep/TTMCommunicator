import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable, HandlesIOException {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;                                                                       //korzystamy z socketa jako parametr aby moc wybrac port
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); //inicjalizuje wejscie dzieki ktoremu serwer odbiera dane od klientow
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));   //inicjalizuje wyjscie dzieki ktoremu server moze wysylac wiadomosci do klientow
            this.clientUsername = bufferedReader.readLine();                                            //pierwsza wiadomosc ktora klient wysyla do serwera to jego nick
//            bufferedReader.readLine();
            clientHandlers.add(this);                                                                  //dodanie obiektu do listy
            broadcastMessage("SERVER: " + clientUsername + " has entered the chat!");     //wyslanie wiadomosci o dolaczeniu do serwera w konstruktorze
        } catch (IOException e) {
            handleIOException(socket, bufferedReader, bufferedWriter);
        }
    }

    @Override
    public void run() {
        String messageFromClient;

        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine(); //czeka na wiadmosc od klienta, zatrzymuje program stad thread
                broadcastMessage(messageFromClient);
            } catch (IOException e) {
                handleIOException(socket, bufferedReader, bufferedWriter);
                break;
            }
        }

    }

    public void broadcastMessage(String messageToSend) {

        for (ClientHandler clientHandler : clientHandlers) {
            try {
                clientHandler.bufferedWriter.write(messageToSend);
                clientHandler.bufferedWriter.newLine();
                clientHandler.bufferedWriter.flush();
            } catch (IOException e) {
                handleIOException(socket, bufferedReader, bufferedWriter);
            }
        }

    }

    private void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + clientUsername + " has left the chat.");
    }

    @Override
    public void handleIOException(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
        System.out.println(clientUsername + " has left the chat.");
        try {
            if (socket != null) {
                socket.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
