package Comunicacion;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import processing.core.PApplet;

public class MultiAdmin extends Thread {

	private PApplet app;
	private InetAddress DIRECCION;
	private int PUERTO;
	private MulticastSocket multicastConection;
	private int id;
	private boolean identified;
	private int turno;
	private int maxRondas, ronda, turnosPorEstacion;
	private int pasiveEnergy;

	private ArrayList<Player> players;

	private int maxPlayers;

	public MultiAdmin(PApplet app) {
		this.app = app;
		players = new ArrayList<Player>();
		maxPlayers = 2;
		turnosPorEstacion = 5;
		maxRondas = turnosPorEstacion*5;
		ronda = 0;
		turno = 0;
		//cambiar dependiendo de la estacion
		pasiveEnergy = 500;
		generateRandomTurn();
	}

	@Override
	public void run() {
		inicializar();

		while (true) {
			try {
				recibir();
			} catch (IOException e) {
				// TODO: handle exception
			}
		}

	}

	public void id() {
		id = -1;

		try {
			multicastConection.setSoTimeout(500);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		enviar("Identify");
		while (identified == false) {

			try {
				recibir();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				identified = true;
				if (id == -1) {
					id = 0;
				}
				try {
					multicastConection.setSoTimeout(0);
				} catch (SocketException e2) {
					// TODO Auto-generated catch block

				}
				System.out.println("Im player: " + id);
			}

		}

	}

	public void inicializar() {

		try {

			PUERTO = 5500;
			DIRECCION = InetAddress.getByName("224.0.0.0");
			multicastConection = new MulticastSocket(PUERTO);
			multicastConection.joinGroup(DIRECCION);
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public void enviar(String message) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				DatagramPacket envio = new DatagramPacket(message.getBytes(), message.length(), DIRECCION, PUERTO);

				try {
					multicastConection.send(envio);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void recibir() throws IOException {
		byte[] buffer = new byte[100];

		DatagramPacket recibo = new DatagramPacket(buffer, buffer.length);
		multicastConection.receive(recibo);

		String recibido = new String(recibo.getData()).trim();
		recibirMensajes(recibido);
	}

	public ArrayList<Integer> generateRandomTurn() {
		ArrayList<Integer> turnos = new ArrayList<Integer>();
		int[] randoms = new int[maxPlayers];

		for (int i = 0; i < randoms.length; i++) {
			randoms[i] = i;
			turnos.add(randoms[i]);
		}

		Collections.shuffle(turnos);

		return turnos;
	}

	public void recibirMensajes(String msgReceived) {

		if (msgReceived.contains("ImInicial")) {
			String[] separated = msgReceived.split(":");
			Player player = new Player(app);

			System.out.println(msgReceived);

			int[] datos = new int[separated.length];

			for (int i = 0; i < datos.length; i++) {
				try {
					datos[i] = Integer.parseInt(separated[i]);
				} catch (NumberFormatException e) {
					datos[i] = 0;
				}
			}

			player.getData(datos[2], datos[3], datos[4]);
			if (players.size() < maxPlayers) {
				players.add(player);
			}

			if (players.size() == maxPlayers) {
				for (int i = 0; i < players.size(); i++) {
					players.get(i).setCanPlay(true);
				}
				enviar("IMCanPlay:true");

				ArrayList<Integer> num = generateRandomTurn();
				System.out.println(num);

				for (int i = 0; i < players.size(); i++) {
					enviar("AsignTurn:" + i + ":" + num.get(i));
				}

			}

		}

		if (msgReceived.contains("IMNombre")) {
			String[] separated = msgReceived.split(":");
			int id = Integer.parseInt(separated[1]);
			players.get(id).setNombre(separated[2]);

		}

		if (msgReceived.contains("IMPob")) {
			String[] separated = msgReceived.split(":");
			int id = Integer.parseInt(separated[1]);
			System.out.println(id + " , " + players.size());
			int poblacion = Integer.parseInt(separated[2]);
			players.get(id).setPoblacion(poblacion);

		}
		if (msgReceived.contains("IMDem")) {
			String[] separated = msgReceived.split(":");
			int id = Integer.parseInt(separated[1]);
			int demanda = Integer.parseInt(separated[2]);
			players.get(id).setDemanda(demanda);

		}
		if (msgReceived.contains("termine")) {
			turno++;
			for (int i = 0; i < players.size(); i++) {
				players.get(i).turnobByAdmin = turno;
			}
			if (turno == maxPlayers) {
				turno = 0;
				
				ArrayList<Integer> num = generateRandomTurn();
				System.out.println(num);

				for (int i = 0; i < players.size(); i++) {
					players.get(i).turno = num.get(i);
					enviar("AsignTurn:" + i + ":" + num.get(i));
				}
				
				ronda ++;
				if (ronda == turnosPorEstacion) 
					enviar ("autumn");
				if (ronda == turnosPorEstacion*2) 
					enviar ("winter");
				if (ronda == turnosPorEstacion*3) 
					enviar ("spring");
				if (ronda == turnosPorEstacion*4) 
					enviar ("juegoTerminado");
				
				enviar("EnergyByRound:" + pasiveEnergy);
			}
			enviar("cambioTurno:" + turno);
		}

	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}

}
