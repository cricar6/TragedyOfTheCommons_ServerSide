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
import java.util.Observable;

import Writing.WriteFile;
import processing.core.PApplet;

public class MultiAdmin extends Observable implements Runnable {

	private PApplet app;
	private InetAddress DIRECCION;
	private int PUERTO;
	private MulticastSocket multicastConection;
	private int id;
	private boolean identified;
	private int turno;
	private int maxRondas, ronda, turnosPorEstacion;
	private int pasiveEnergy;
	private int turnoTotal;
	private int energiaTotal;
	private float arbolesTotal;
	private int energiaRonda;
	private String season;
	
	private boolean juegoTerminado = false;

	private ArrayList<Player> players;
	private ArrayList<ArrayList<String>> playersVarsPop;
	private ArrayList<ArrayList<String>> playersVarsHappi;

	private WriteFile writerPopulation;
	private WriteFile writerHappiness;
	
	private int maxPlayers;

	public MultiAdmin(PApplet app) {
		this.app = app;
		players = new ArrayList<Player>();
		playersVarsPop = new ArrayList<ArrayList<String>>();
		playersVarsHappi = new ArrayList<ArrayList<String>>();
		maxPlayers = 3;
		turnosPorEstacion = 5;
		maxRondas = turnosPorEstacion * 5;
		ronda = 0;
		turno = 0;
		turnoTotal = 22;
		energiaRonda = pasiveEnergy * players.size();
		arbolesTotal = 0;
		energiaTotal = energiaRonda + ((int) (arbolesTotal / 100) * energiaRonda);
		// cambiar dependiendo de la estacion
		season = "summer";

		if (season == "summer") {
			pasiveEnergy = 2500;
		} else if (season == "autumn") {
			pasiveEnergy = 2300;
		} else if (season == "winter") {
			pasiveEnergy = 1700;
		} else if (season == "spring") {
			pasiveEnergy = 2400;
		}
		generateRandomTurn();
		
		
		writerPopulation = new WriteFile("population");
		Thread p = new Thread(writerPopulation);
		p.start();

		writerHappiness = new WriteFile("happiness");
		Thread h = new Thread(writerHappiness);
		h.start();
		
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
					System.out.println("enviando");
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
			if (players.size() <= maxPlayers) {
				players.add(player);
				
				playersVarsPop.add(new ArrayList<String>());
				playersVarsHappi.add(new ArrayList<String>());

				energiaRonda = players.size() * pasiveEnergy;
				energiaTotal = energiaRonda + ((int) (arbolesTotal / 100) * energiaRonda);

			}

			if (players.size() == maxPlayers) {
				for (int i = 0; i < players.size(); i++) {
					players.get(i).setCanPlay(true);
				}
				

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
		if (msgReceived.contains("IMFel")) {
			String[] separated = msgReceived.split(":");
			int id = Integer.parseInt(separated[1]);
			int felicidad = Integer.parseInt(separated[2]);
			players.get(id).setFelicidad(felicidad);

		}
		if (msgReceived.contains("Solicitar")) {
			String[] separated = msgReceived.split(":");
			int id = Integer.parseInt(separated[1]);
			int solicitando = Integer.parseInt(separated[2]);
			
			if (solicitando <= energiaTotal) {
				//Enviar energia con lo solicitado
				System.out.println("Enviaré: " + solicitando);
				energiaTotal= energiaTotal - solicitando;
				enviar("Solicitado:" + id + ":" + solicitando);

				
			} if (solicitando > energiaTotal) {
				//Enviar 0 de energia
				System.out.println("Enviaré: 0");
				enviar("Solicitado:" + id + ":" + 0);
			}
		}
		
		if (msgReceived.contains("IMEnv")) {
			String[] separated = msgReceived.split(":");
			int id = Integer.parseInt(separated[1]);
			int environmental = Integer.parseInt(separated[2]);
			players.get(id).setArboles(environmental);

			int arbolitos = 0;
			for (int i = 0; i < players.size(); i++) {
				arbolitos = arbolitos + players.get(i).getArboles();
			}
			arbolitos = arbolitos * 3;
			arbolesTotal = arbolitos;

		}
		if (msgReceived.contains("termine")) {
			turno++;

			//energiaTotal -= pasiveEnergy;

			for (int i = 0; i < players.size(); i++) {
				players.get(i).turnobByAdmin = turno;
			}

			if (season == "summer") {
				pasiveEnergy = 2500;
			} else if (season == "autumn") {
				pasiveEnergy = 2300;
			} else if (season == "winter") {
				pasiveEnergy = 1700;
			} else if (season == "spring") {
				pasiveEnergy = 2400;
			}

			if (turno == maxPlayers) {
				turno = 0;

				terminarTurno();
				ArrayList<Integer> num = generateRandomTurn();
				System.out.println(num);

				for (int i = 0; i < players.size(); i++) {
					players.get(i).turno = num.get(i);
					enviar("AsignTurn:" + i + ":" + num.get(i));
				}

				ronda++;
				if (ronda == turnosPorEstacion) {
					enviar("autumn");
					season = "autumn";
				}
				if (ronda == turnosPorEstacion * 2) {
					enviar("winter");
					season = "winter";
				}
				if (ronda == turnosPorEstacion * 3) {
					enviar("spring");
					season = "spring";
				}
				if (ronda == turnosPorEstacion * 4) {
					enviar("juegoTerminado");
				}
				//Comprobar envio si hay suficiente energia
				enviar("EnergyByRound:" + pasiveEnergy);
				energiaRonda = pasiveEnergy * players.size();
				
				
				energiaTotal = energiaRonda + (int) ((arbolesTotal / 100) * energiaRonda);
				System.out.println(((float) arbolesTotal / 10) + "percentage");
				System.out.println((int) ((arbolesTotal / 100) * energiaRonda));

			}
			enviar("cambioTurno:" + turno);

		}

	}

	public void terminarTurno () {

		for (int i = 0; i < players.size(); i++) {
			playersVarsPop.get(i).add(players.get(i).getPoblacion()+"");
			playersVarsHappi.get(i).add(players.get(i).getFelicidad()+"");

		}
		
		if (ronda == maxRondas) {
			terminarJuego();
		}

		System.out.println(playersVarsPop+ "pop");
		System.out.println(playersVarsHappi+ "happi");
	}
	
	public void terminarJuego() {
		
		writerPopulation.setPlayers(playersVarsPop);
		writerPopulation.run();
		writerHappiness.setPlayers(playersVarsHappi);
		writerHappiness.run();
		
		juegoTerminado = true;
	}
	
	
	
	public boolean isJuegoTerminado() {
		return juegoTerminado;
	}

	public void setJuegoTerminado(boolean juegoTerminado) {
		this.juegoTerminado = juegoTerminado;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}

	public int getTurno() {
		return turno;
	}

	public void setTurno(int turno) {
		this.turno = turno;
	}

	public int getMaxRondas() {
		return maxRondas;
	}

	public void setMaxRondas(int maxRondas) {
		this.maxRondas = maxRondas;
	}

	public int getRonda() {
		return ronda;
	}

	public void setRonda(int ronda) {
		this.ronda = ronda;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTurnoTotal() {
		return turnoTotal;
	}

	public void setTurnoTotal(int turnoTotal) {
		this.turnoTotal = turnoTotal;
	}

	public int getEnergiaTotal() {
		return energiaTotal;
	}

	public void setEnergiaTotal(int energiaTotal) {
		this.energiaTotal = energiaTotal;
	}

	public float getArbolesTotal() {
		return arbolesTotal;
	}

	public void setArbolesTotal(int arbolesTotal) {
		this.arbolesTotal = arbolesTotal;
	}

	public int getEnergiaRonda() {
		return energiaRonda;
	}

	public void setEnergiaRonda(int energiaRonda) {
		this.energiaRonda = energiaRonda;
	}

	public String getSeason() {
		return season;
	}

	public void setSeason(String season) {
		this.season = season;
	}

	public ArrayList<ArrayList<String>> getPlayersVarsPop() {
		return playersVarsPop;
	}

	public void setPlayersVarsPop(ArrayList<ArrayList<String>> playersVarsPop) {
		this.playersVarsPop = playersVarsPop;
	}

	public ArrayList<ArrayList<String>> getPlayersVarsHappi() {
		return playersVarsHappi;
	}

	public void setPlayersVarsHappi(ArrayList<ArrayList<String>> playersVarsHappi) {
		this.playersVarsHappi = playersVarsHappi;
	}


}
