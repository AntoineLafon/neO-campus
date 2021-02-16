package captors;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import bdd.BaseDonnes;

public class CaptorSocket extends Captor implements Runnable {

	Socket socket;
	BaseDonnes bdd;
	BufferedReader buffer;
	float value;
	private List<CaptorListener> listeners = new ArrayList<CaptorListener>();

	public CaptorSocket(Socket socket, BaseDonnes bdd) throws IOException {
		super(null, null, null, 0, null, 0, 0);
		this.socket = socket;
		this.bdd = bdd;
		try {
			buffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}

	/************* gestion listeners *******************/

	public void addListeners(CaptorListener l) {
		listeners.add(l);
	}

	private void tellCaptorIsDisconnected() {
		for (CaptorListener cl : listeners) {
			cl.captorDisconnected(this);
		}
	}

	private void tellCaptorIsConnected() {
		for (CaptorListener cl : listeners) {
			cl.captorConnected(this);
		}
	}

	private void tellValueChanged() {
		for (CaptorListener cl : listeners) {
			cl.captorValueChanged(this);
		}
	}

	/****************** Thread ***********************/

	public void run() {
		if (connecter()) {
			tellCaptorIsConnected();
			String msg;
			String[] splittedMsg;
			boolean ended = false;
			while (!ended) {
				try {
					msg = buffer.readLine();
					if (msg != null) {
						// System.out.println(msg);
						splittedMsg = msg.split(" ");
						switch (splittedMsg[0]) {
						case "Donnee":
							if (Float.parseFloat(splittedMsg[2]) != value) {
								value = Float.parseFloat(splittedMsg[2]);
								tellValueChanged();
							}
							break;
						case "Deconnexion":
							ended = true;
							break;
						}
					}
				} catch (Exception e) {
					ended = true;
				}
			}
		}
		try {
			socket.close();
			tellCaptorIsDisconnected();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// attent de recevoir un message de "connexion" de la part du capteur
	private boolean connecter() {
		// TODO ajouter timeout si connexion trop longue...
		boolean connected = false;
		String msg;
		String[] splittedMsg;
		String[] parametres;
		while (!connected) {
			try {
				msg = buffer.readLine();
				if (msg != null) {
					System.out.println(msg);
					splittedMsg = msg.split(" ");
					if (splittedMsg.length > 0 && splittedMsg[0].equals("Connexion")) {
						parametres = splittedMsg[2].split(":");
						String nom = splittedMsg[1];
						boolean trouve = false;
						for (Captor c : bdd.recupListeCapteursComplete()) {
							if (c.getNom().equals(nom)) {
								min = c.getMin();
								max = c.getMax();
								type = c.getType();
								batiment = c.getBatiment();
								etage = c.getEtage();
								lieu = c.getLieu();
								this.nom = c.getNom();
								trouve = true;
							}
						}

						if (!trouve) {
							String batiment = parametres[1];
							int etage = Integer.parseInt(parametres[2]);
							String lieu = parametres[3];
							TypeMesure type;
							switch (parametres[0]) {
							case "EAU":
								type = TypeMesure.WATER;
								min = 0;
								max = 10;
								break;
							case "ELECTRICITE":
								type = TypeMesure.ELECTRICITY;
								min = 10;
								max = 500;
								break;
							case "TEMPERATURE":
								type = TypeMesure.TEMPERATURE;
								min = 17;
								max = 22;
								break;
							default:
								type = TypeMesure.PRESSURIZEDAIR;
								min = 0;
								max = 5;
								break;
							}
							this.nom = nom;
							this.batiment = batiment;
							this.etage = etage;
							this.type = type;
							this.lieu = lieu;
						}
						connected = true;
						System.out.println("Thread: j'ai connecte le capteur, il est de type " + this.type);
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		System.out.println("Capteur connecté!");
		return true;
	}

	public float getValue() {
		return value;
	}

	public boolean isTheValueCorrect() {
		return value <= this.getMax() && this.getMin() <= value;
	}

	public boolean isTheValueTooHigh() {
		return value > this.getMax();
	}

}
