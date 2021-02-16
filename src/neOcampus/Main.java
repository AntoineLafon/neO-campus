package neOcampus;
import graphics.*;

import java.net.ServerSocket;
import java.net.Socket;

import bdd.BaseDonnes;
import captors.*;

public class Main extends Thread{
	
	static int port;
	private static NeoCampusFrame fenetre;
	public static void main(String[] args) {
		try {

			
			
			/* creation fenetre */
			BaseDonnes bddonnees = new BaseDonnes(); //a commenter pour desactiver la bdd--------------
			fenetre = new NeoCampusFrame(bddonnees);
			port = fenetre.getPort(8952);
			ServerSocket server = new ServerSocket(port);
			
			
			/*gestion des connexions entrantes*/
			while(!server.isClosed()) {
				Socket socket = server.accept();
				System.out.println("Nouveau client!");
				try {
				CaptorSocket captor = new CaptorSocket(socket, bddonnees);
				fenetre.ecouter(captor); //ajoute la fenetre en ecoute sur le capteur
				captor.addListeners(bddonnees); //a commenter pour desactiver la bdd----------------
				Thread t = new Thread(captor);
				t.start();
				}catch (Exception e) {
					e.printStackTrace();
					// TODO: handle exception
					server.close();
				}
			}
			
			
		}catch (Exception e) {
			System.exit(1);
		}
	}
	
}
