import java.util.ArrayList;

import Comunicacion.MultiAdmin;
import Comunicacion.Player;
import processing.core.PApplet;

public class Logic {

	private PApplet app;
	private int screen;

	private MultiAdmin admin; 
	
	private ArrayList<Player> players;
	
	
	public Logic(PApplet app) {
		this.app = app;
		screen = 0;
	
		
		admin = new MultiAdmin(app);
		admin.start();
		
		players = admin.getPlayers();
		
		
	}

	public void display() {
		
		//System.out.println(players.size());

		for (int i = 0; i < players.size(); i++) {
			players.get(i).displayData();
			players.get(i).mover(i*100, 100);
			
		}
		
		switch (screen) {
		case 0:
			
			break;

		case 1:
			
			break;

		}
	}

	public void moved() {
		// TODO Auto-generated method stub
		
	}

	public void clicked() {
		// TODO Auto-generated method stub
		
	}

	public void dragged() {
		// TODO Auto-generated method stub
		
	}

	public void released() {
		// TODO Auto-generated method stub
		
	}

	public void kpress() {
		// TODO Auto-generated method stub
		
	}

}
