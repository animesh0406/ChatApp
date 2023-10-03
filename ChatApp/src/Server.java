import javax.sound.midi.Soundbank;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.*;
import java.util.*;
import java.net.*;
import java.io.*;
import javax.swing.*;
public class Server extends JFrame{
    ServerSocket server;
    Socket socket;
    //take two variable to read and write
    BufferedReader br;
    PrintWriter out;
    private JLabel heading =new JLabel("Server Area");
    private JTextArea messageArea=new JTextArea();
    private JTextField messageInput=new JTextField();
    private Font font= new Font("Roboto",Font.PLAIN,20);
    public Server(){// to make a socketServer
        try{
            server=new ServerSocket(8001);
            System.out.println("server is ready to accept connection(listening)");
            System.out.println("waiting");
            socket=server.accept();
            br =new BufferedReader(new InputStreamReader(socket.getInputStream()));//the socket object gives an stream of input(inputstream or data to server which is read by buffereReader)
            out=new PrintWriter(socket.getOutputStream());//this prints the data to the client

            //startReading(); /// below tasks to be completetd simultaneously
            //startWriting();
            createGUI();
            handleEvents();

            startReading();

       }
        catch(Exception e){
            e.printStackTrace();
        }

    }
    private void handleEvents(){
        messageInput.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                //System.out.println("Key released "+e.getKeyCode());
                if(e.getKeyCode()==10){
                    //System.out.println("you have pressed enter button");
                    String contentToSend=messageInput.getText();
                    messageArea.append("Me : "+contentToSend+"\n");
                    out.println(contentToSend);
                    out.flush();
                    messageInput.setText("");
                    messageArea.requestFocus();
                }
            }
        });
    }
    private void createGUI(){
        this.setTitle("Server Messenger[Server End]");
        this.setSize(500,700);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //coding for components
        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);
        //heading.setIcon(new ImageIcon("iconn.png"));
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        messageArea.setEditable(false);
        //messageInput.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
        //frame layout
        this.setLayout(new BorderLayout());

        //adding the components to layout
        this.add(heading,BorderLayout.NORTH);
        JScrollPane jScrollPane=new JScrollPane(messageArea);
        this.add(jScrollPane,BorderLayout.CENTER);
        this.add(messageInput,BorderLayout.SOUTH);
        this.setVisible(true);
    }
    public void startReading(){
        // thread-> read krta rhega
        Runnable r1=()->{
            System.out.println("Reader started......");
            try{
                while(true){

                    String message=br.readLine();
                    if(message.equals("bye")){
                        System.out.println("Client terminated the chat");
                        JOptionPane.showMessageDialog(null,"Client terminated the chat");
                        messageInput.setEnabled(false);
                        socket.close();
                        break;
                    }
                    messageArea.append("Client : "+message+"\n");
                    //System.out.println("Client : "+message);


                }
            }
            catch(Exception e){
                e.printStackTrace();
            }

        }; //lambda function
        new Thread(r1).start();
    }
    public void startWriting(){
         //thread -> data user se lega and then usko send krega client tk
        Runnable r2=()->{
            System.out.println("Writer started");
            try{
                while( !socket.isClosed()){

                    BufferedReader br1=new BufferedReader(new InputStreamReader(System.in));
                    String content=br1.readLine();

                    out.println(content);
                    out.flush();
                    if(content.equals("bye")){
                        socket.close();
                        break;
                    }
                }

            }
           catch(Exception e){
                e.printStackTrace();
           }


        };
        new Thread(r2).start();
    }
    public static void main(String[] args) {
        System.out.println("this is server......going to start server");
        new Server();//calling a server
    }
}
