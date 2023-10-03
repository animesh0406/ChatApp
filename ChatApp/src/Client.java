import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.security.Key;

public class Client extends JFrame{

    Socket socket;

    BufferedReader br;
    PrintWriter out;
    //Declare Components
    private JLabel heading =new JLabel("Client Area");
    private JTextArea messageArea=new JTextArea();
    private JTextField messageInput=new JTextField();
    private Font font= new Font("Roboto",Font.PLAIN,20);
    public Client(){

        try{

            System.out.println("Sending request to server...");
            socket=new Socket("127.0.0.1",8001); // the ip of server;
            System.out.println("connection is done");
            br =new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out=new PrintWriter(socket.getOutputStream());

            createGUI();
            handleEvents();

            startReading();
            //startWriting();

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
        this.setTitle("Client Messenger[Client End]");
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
        Runnable r1=()->{
            try{
                while(true){

                    String message=br.readLine();
                    if(message.equals("bye")){
                        System.out.println("Server terminated the chat");
                        JOptionPane.showMessageDialog(null,"Server terminated the chat");
                        messageInput.setEnabled(false);
                        socket.close();
                        break;
                    }

                    messageArea.append("Server : "+message+"\n");
                    //System.out.println("Server : "+message);

                }

            }
            catch(Exception e){
                e.printStackTrace();
            }


        };
        new Thread(r1).start();
    }
    public void startWriting(){
        Runnable r2=()->{
            System.out.println("Writer started");
            try{
                while(!socket.isClosed()){

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
        System.out.println("this is client....");
        new Client();
    }
}
