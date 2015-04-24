/**
 *
 * Maeda Hanafi
 * Assignment #3
 * 4/25/11
 */


import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletOutputStream;

public class LoginServlet extends HttpServlet {
  String username;
  String password;
 
    /** Process the HTTP Post request */
    public void doPost (HttpServletRequest request, HttpServletResponse
        response) throws ServletException, IOException {
        response.setContentType("text/html");
        ServletOutputStream out= response.getOutputStream();

        // Obtain parameters from the client
         if(username==null && password==null){
            password = request.getParameter("password");
            username = request.getParameter("username");
        }
        

        String messageNumber = request.getParameter("message");

        //form that asks for message number if user wants to retrieve
        out.println("<form action = \"http://localhost:8080/ServletEmailVers2/LoginServlet\" method = \"post\"><p> <label>Retrieve Message Number:</label><input type = \"text\" name = \"message\" size = \"8\" /><!-- Submit and Reset buttons --><p><input type = \"submit\" value = \"Submit\" /><input type = \"reset\" value = \"Reset\" /></p></form>");

        if(messageNumber!=null){
            out.println("<p>MessageNumber:"+Integer.parseInt(messageNumber)+"</p>");
        }else{
            messageNumber = "0";
        }

        viewMessages(username, password, Integer.parseInt(messageNumber), out);

        out.close(); // Close stream
    }

    public void viewMessages(String username, String password, int messageNumber, ServletOutputStream out) throws IOException{

        java.util.Properties props = new java.util.Properties();
        props.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.imap.socketFactory.fallback", "false");
        props.setProperty("mail.imap.socketFactory.port", "993");
        props.setProperty("mail.imap.host", "imap.gmail.com");


        String protocol = "imap";

        try{
            Session mySession = Session.getDefaultInstance (props);
            Store myStore = mySession.getStore(protocol);
            myStore.connect ( username, password);

            Folder myFolder = myStore.getFolder("INBOX");
            //  Other folders too

            System.out.println ("Accessing mail account now");
            out.println("Accessing mail account now");

            myFolder.open(Folder.READ_ONLY);

            int messagecount = myFolder.getMessageCount();
            System.out.println (myFolder.getFullName() + " has " + messagecount + " messages.");
            out.println(myFolder.getFullName() + " has " + messagecount + " messages.");

            Message[] message = myFolder.getMessages (1, messagecount);
            for (int i = 0; i < message.length; i++){
                Address[] fromAddr = message[i].getFrom();
                if(i+1==messageNumber){
                    out.println("<p>"+fromAddr[0] + ":" + message[i].getSubject()+"</p>");
                    message[i].writeTo(out);
                }
            }
            out.println("<p>List of Messages</p>");
            for (int i = 0; i < message.length; i++){
                Address[] fromAddr = message[i].getFrom();
                System.out.println (fromAddr[0] + ":" + message[i].getSubject());
                out.println((i+1)+"<p>"+fromAddr[0] + ":" + message[i].getSubject()+"</p>");
               
            }

            // Close messages, don't expunge
            myFolder.close(false);
            myStore.close();
        }catch (MessagingException me){
            System.err.println ("Messaging failure : " + me);
            out.println("Messaging failure : " + me);
        }catch (Exception ex){
            System.err.println ("Failure : " + ex);
            out.println("Failure : " + ex);
        }
    }

}

