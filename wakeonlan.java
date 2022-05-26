import java.net.*;
import javax.swing.*;
import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.UIManager;
import javax.swing.UIDefaults;

class WoL {
	private final int PORT = 9;    
	private String BroadcastIP;
	private String MAC;
	
	public void setBroadcastIP(String BroadcastIP) { 
		this.BroadcastIP = BroadcastIP; 
	}
	
	public String getBroadcastIP() { 
		return this.BroadcastIP; 
	}
	
	public void setMAC(String MAC) { 
		this.MAC = MAC; 
	}
	
	public String getMAC() { 
		return this.MAC; 
	}
	
	private byte[] getMacBytes(String macStr) {
        byte[] bytes = new byte[6];
        String[] hex = macStr.split("(\\:|\\-)");

        if (hex.length != 6) {
            throw new IllegalArgumentException("Invalid MAC address.");
        }

        try {
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) Integer.parseInt(hex[i], 16);
            }
        } catch (NumberFormatException e) {
            throw e;
        }
        
        return bytes;
    }

	public static boolean validIP (String ip) {
    	try {
        	if (ip == null || ip.isEmpty()) {
            	return false;
        	}

        	String[] parts = ip.split("\\.");
        	if (parts.length != 4) {
            	return false;
        	}

        	for(String s : parts) {
            	int i = Integer.parseInt( s );
            	if ((i < 0) || (i > 255)) {
                	return false;
            	}
        	}

        	if (ip.endsWith(".")) {
            	return false;
        	}

        	return true;
    	} catch(NumberFormatException e) {
        	return false;
    	}
	}
	
	WoL(String BroadcastIP, String MAC) {
		   this.setBroadcastIP(BroadcastIP);
		   this.setMAC(MAC);  
	}
	
	public void sendMagicPaket() throws Exception, IllegalArgumentException {
		try {
            byte[] macBytes = getMacBytes(this.MAC);
            byte[] bytes = new byte[6 + 16 * macBytes.length];
			InetAddress address;
            
            for(int i = 0; i < 6; i++) {
                bytes[i] = (byte) 0xff;
            }
            
            for(int i = 6; i < bytes.length; i += macBytes.length) {
                System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
            }
            
			try {
				if(!validIP(this.BroadcastIP)) {
					throw new IllegalArgumentException("Not a valid IP address.");
				} else {
            		address = InetAddress.getByName(this.BroadcastIP);
				}
			} catch(IllegalArgumentException e) {
				throw e;
			}
			
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, PORT);
            DatagramSocket socket = new DatagramSocket();

            socket.send(packet);

            socket.close();
            
            System.out.println("Wake-on-LAN packet sent.");
        } catch(IllegalArgumentException e) {
			throw e;
        } catch (Exception e) {
			throw e;
        }
	}
}

class WoLGUI {
	private final String AppName = "WakeOnLan";
	private final String AppVersion = "0.1-beta";
	
	private JFrame frame;
	private JPanel panel;
	private JButton btnSendPaket;
	private JLabel lblMACAddr;
	private JLabel lblBroadcastAddr;
	private JTextField txtMACAddr;
	private JTextField txtBroadcastAddr;
	
	private GridBagConstraints setConstraints(int x, int y, int width) {
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.HORIZONTAL;
		
		c.gridx = x;
		c.gridy = y;
		c.gridwidth = width;
		
		c.insets = new Insets(3, 3, 3, 3);
		
		return c;
	}
	
	private void clearField(JTextField field) {
		field.setText("");
	}
	
	private void showError(Exception e) {
		JOptionPane.showMessageDialog(frame, e.getMessage(), "Error",
									  JOptionPane.ERROR_MESSAGE);
	}
	
	private void setupButtonHandler() {
		btnSendPaket.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				WoL wol = new WoL(txtBroadcastAddr.getText().toString(), 
								  txtMACAddr.getText().toString());
				
				try {
					wol.sendMagicPaket();
				} catch(Exception e) {
					showError(e);
				}
				
				clearField(txtBroadcastAddr);
				clearField(txtMACAddr);
				
				wol = null;
			}
		});
	}
	
	private void setupUI() {
		frame = new JFrame(this.AppName);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setSize(240, 200);
		frame.setResizable(false);
		
		panel = new JPanel(new GridBagLayout());
		frame.setContentPane(panel);
		
		lblMACAddr = new JLabel("MAC Address:");
		panel.add(lblMACAddr, setConstraints(0, 0, 1));
		
		txtMACAddr = new JTextField(17);
		panel.add(txtMACAddr, setConstraints(0, 1, 2));
		
		lblBroadcastAddr = new JLabel("Broadcast Address:");
		panel.add(lblBroadcastAddr, setConstraints(0, 2, 2));
		
		txtBroadcastAddr = new JTextField(100);
		panel.add(txtBroadcastAddr, setConstraints(0, 3, 2));
		
		btnSendPaket = new JButton("Send");
		panel.add(btnSendPaket, setConstraints(0, 4, 1));
		
		setupButtonHandler();
	}
	
	WoLGUI() {
		setupUI();
		
		frame.setVisible(true);
	}
}

class wakeonlan {
	public static void main(String[] args) {
		
		WoLGUI w = new WoLGUI();
				
	}
}