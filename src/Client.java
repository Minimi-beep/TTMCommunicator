import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;


public class    Client implements HandlesIOException {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    private JFrame frame;
    private JPanel topPanel;
    private JPanel bottomPanel;
    private JTextArea textArea;
    private JTextField textField;

    public Client(Socket socket) {
        this.socket = socket;
        this.username = null;
        try {
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            handleIOException(socket, bufferedReader, bufferedWriter);
        }

        frame = new JFrame("Type in your username to start!");
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.setBackground(Color.PINK);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setMargin(new Insets(10, 10, 10, 10));
        textArea.setPreferredSize(new Dimension(0, 320));
        textArea.setBackground(Color.YELLOW);
        topPanel.add(textArea, BorderLayout.CENTER);

        textField = new JTextField();
        textField.setMargin(new Insets(10, 10, 10, 10));
        textField.addActionListener(e -> {
            String message = textField.getText();
            sendMessage(message);
            textField.setText("");
        });
        textField.setBackground(Color.ORANGE);
        bottomPanel.add(textField, BorderLayout.CENTER);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.setVisible(true);

    }

    public void sendMessage(String message) {
        try {
            if(username == null) {
                username = message;
                frame.setTitle(username + "'s chat");
                bufferedWriter.write(message);

            } else {
                bufferedWriter.write(username + ": " + message);
            }
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            handleIOException(socket, bufferedReader, bufferedWriter);
        }
    }

    public void listenForMessage() {
        new Thread(() -> {
            String messageFromGroupChat;

            while (socket.isConnected()) {
                try {
                    messageFromGroupChat = bufferedReader.readLine();
                    textArea.append(messageFromGroupChat + "\n");
                } catch (IOException e) {
                    handleIOException(socket, bufferedReader, bufferedWriter);
                }
            }
        }).start();
    }

    @Override
    public void handleIOException(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
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

    public static void main(String[] args) throws IOException {

        Socket socket = new Socket("localhost", 1234);
        Client client = new Client(socket);
        client.listenForMessage();

    }
}
