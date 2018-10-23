package Comunicacion;

import processing.core.PApplet;

public class Player extends Thread {

	private PApplet app;
	private int posX, posY;
	protected int poblacion, felicidad, demanda, arbol, arboles;
	protected String nombre;
	protected int season, energiaGeneral, turno, turnobByAdmin;
	protected String notificacion;
	protected boolean canPlay =false;

	public Player(PApplet app) {
		this.app = app;
		this.arbol=0;
		arboles = 0;
	}

	public void getData(int poblacion, int demanda, int energiaGeneral) {
		this.poblacion = poblacion;
		this.demanda = demanda;
		this.energiaGeneral = energiaGeneral;
		
		
		nombre = "";
	}

	@Override
	public void run() {

	}

	public void mover(int posX, int posY) {
		this.posX = posX;
		this.posY = posY;
	}

	public void displayData() {
		app.fill(0);
		app.text(poblacion, posX, posY);
		app.text(demanda, posX, posY+20);
		app.text(energiaGeneral, posX, posY+40);
		app.text(nombre, posX, posY+60);
		if (turnobByAdmin == turno) {
			app.text("Es tu turno", posX, posY+80);
		} 
	}
	
	

	public int getArbol() {
		return arbol;
	}

	public void setArbol(int arbol) {
		this.arbol = arbol;
	}

	public int getPoblacion() {
		return poblacion;
	}

	public void setPoblacion(int poblacion) {
		this.poblacion = poblacion;
	}

	public int getFelicidad() {
		return felicidad;
	}

	public void setFelicidad(int felicidad) {
		this.felicidad = felicidad;
	}

	public int getDemanda() {
		return demanda;
	}

	public void setDemanda(int demanda) {
		this.demanda = demanda;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getSeason() {
		return season;
	}

	public void setSeason(int season) {
		this.season = season;
	}

	public int getEnergiaGeneral() {
		return energiaGeneral;
	}

	public void setEnergiaGeneral(int energiaGeneral) {
		this.energiaGeneral = energiaGeneral;
	}

	public int getTurno() {
		return turno;
	}

	public void setTurno(int turno) {
		this.turno = turno;
	}

	public String getNotificacion() {
		return notificacion;
	}

	public void setNotificacion(String notificacion) {
		this.notificacion = notificacion;
	}

	public boolean isCanPlay() {
		return canPlay;
	}

	public void setCanPlay(boolean canPlay) {
		this.canPlay = canPlay;
	}

	public int getArboles() {
		return arboles;
	}

	public void setArboles(int arboles) {
		this.arboles = arboles;
	};

	
	
}
