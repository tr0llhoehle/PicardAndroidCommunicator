package de.tr0llhoehle.android.picardcommunicator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.json.*;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class Communicator {
	
	private Activity context;

	private boolean authenticated;
	
	public Communicator(Activity context) {
		this.context = context;
		this.authenticated = false;
	}
	
	public void authenticate() throws IOException, JSONException {
		this.authenticated = false;

        CommandSender sender = new CommandSender();





		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.context);
    	String username = sharedPref.getString("username", "");
    	String password = sharedPref.getString("password", "");
    	String ip = sharedPref.getString("ip_address", "");
        sender.execute(username, password, ip);


	}

    private class CommandSender extends AsyncTask<String, Integer, Long> {
        private int port = 5005;
        private DatagramSocket socket;
        private boolean running;
        private int direction; //from 10 to -10 right to left
        private int speed; //from 10 to -10 low to high
        protected Long doInBackground(String... params){
            try{
                running = false;
                String username = params[0];
                String password = params[1];
                String ip = params[2];
                InetAddress addr = InetAddress.getByName(ip);
                //socket.setReuseAddress(true);
                socket = new DatagramSocket(port);
                String message = "{\"sequence\":0, \"command\":\"requestauth\", \"username\":\""+username+"\"}";
                byte[] buffer = message.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, addr, port);
                socket.send(packet);
                buffer = new byte[1024];
                packet = new DatagramPacket(buffer, buffer.length);

                socket.receive(packet);
                JSONObject obj = new JSONObject(new String(packet.getData()));
                String realm = obj.getString("realm");
                String nonce = obj.getString("nonce");
                String sequence = obj.getString("sequence");
                sequence = Integer.toString(Integer.valueOf(sequence)+1);
                String response = createResponse(username, realm, password, sequence, nonce);
                message = "{\"sequence\":"+sequence+", \"command\":\"auth\", \"username\":\""+username+"\", \"nonce\":\""+nonce+"\", \"response\":\""+response+"\", \"realm\":\""+realm+"\"}";

                buffer = message.getBytes();
                packet = new DatagramPacket(buffer, buffer.length, addr, port);
                socket.send(packet);
                sequence = Integer.toString(Integer.valueOf(sequence)+1);
                buffer = new byte[1024];
                packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                obj = new JSONObject(new String(packet.getData()));
                running = true;
                while(running) {
                    Thread.sleep(500);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }


        public void updateDirection() {

        }

        public void updateSpeed() {

        }

    }
    public static String createResponse(String username, String realm, String password, String sequence, String nonce) {
        String ha1 = md5(username+":"+realm+":"+password);
        String bla = "AUTH:"+sequence;
        String ha2 = md5("AUTH:"+sequence);
        return md5(ha1+":"+nonce+":"+ha2);
    }

    public static final String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}
