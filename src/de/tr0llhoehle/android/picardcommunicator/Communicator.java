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
import android.os.Looper;
import android.preference.PreferenceManager;
import android.widget.Toast;
import android.os.Handler;

public class Communicator {
	
	private Activity context;

	private boolean authenticated;
    private int port = 5005;
    private DatagramSocket socket;
    private InetAddress addr;
    private String username;
    private String password;
    private String realm;
    private String nonce;
    private int sequence;
    private Handler handler;
	
	public Communicator(Activity context) throws IOException, JSONException {
		this.context = context;
		this.authenticated = false;
        this.sequence = 0;
        socket = new DatagramSocket(port);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.context);
        username = sharedPref.getString("username", "");
        password = sharedPref.getString("password", "");
        String ip = sharedPref.getString("ip_address", "");
        addr = InetAddress.getByName(ip);

        authenticate();
	}
	
	public void authenticate() throws IOException, JSONException {
		this.authenticated = false;

        new Authenticator().execute();

	}

    public void updateDirection(int percentage) throws JSONException {
        if(authenticated) {
            JSONObject obj = new JSONObject();
            obj.put("sequence", sequence);
            obj.put("command", "direction");
            obj.put("percentage", percentage*10);
            obj.put("username", username);
            obj.put("hash", createResponse(username, realm, password, sequence, nonce));
            handler.post(new Command(obj));
            sequence = sequence+1;
        }
    }

    public void updateSpeed(int percentage) throws JSONException {
        if(authenticated) {
            JSONObject obj = new JSONObject();
            obj.put("sequence", sequence);
            obj.put("command", "speed");
            obj.put("percentage", percentage*10);
            obj.put("username", username);
            obj.put("hash", createResponse(username, realm, password, sequence, nonce));
            handler.post(new Command(obj));
            sequence = sequence+1;
        }
    }

    private class Command implements Runnable {
        private JSONObject jcommand;
        public Command(JSONObject jcommand) {
            this.jcommand = jcommand;
        }
        public void run() {
            byte[] buffer = jcommand.toString().getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, addr, port);

            try {
                socket = new DatagramSocket();
                socket.send(packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class Authenticator extends AsyncTask<String, Integer, Long> {


        protected Long doInBackground(String... params){
            try{
                String message = "{\"sequence\":0, \"command\":\"requestauth\", \"username\":\""+username+"\"}";
                byte[] buffer = message.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, addr, port);
                socket.send(packet);
                buffer = new byte[1024];
                packet = new DatagramPacket(buffer, buffer.length);

                socket.receive(packet);
                JSONObject obj = new JSONObject(new String(packet.getData()));
                realm = obj.getString("realm");
                nonce = obj.getString("nonce");
                sequence = Integer.valueOf(obj.getString("sequence"));
                sequence = sequence+1;
                String response = createResponse(username, realm, password, sequence, nonce);
                message = "{\"sequence\":"+sequence+", \"command\":\"auth\", \"username\":\""+username+"\", \"nonce\":\""+nonce+"\", \"response\":\""+response+"\", \"realm\":\""+realm+"\"}";

                buffer = message.getBytes();
                packet = new DatagramPacket(buffer, buffer.length, addr, port);
                socket.send(packet);
                sequence = sequence+1;
                buffer = new byte[1024];
                packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                obj = new JSONObject(new String(packet.getData()));
                authenticated = true;
                Looper.prepare();
                handler = new Handler();
                Looper.loop();
            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }


    }
    public static String createResponse(String username, String realm, String password, int sequence, String nonce) {
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
