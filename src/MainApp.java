
import processing.core.PApplet;

public class MainApp extends PApplet {
	
	private Logic logic;

	public static void main(String[] args) {
		PApplet.main("MainFiles.MainApp");
	}
	
	@Override 
	public void settings() {
		size (1200, 700, P3D);
	}
	
	@Override
	public void setup() {

		logic = new Logic (this) ;
	}
	
	@Override 
	public void draw() {
		background (184,188,188);
		smooth();
		logic.display();
	}
	
	@Override 
	public void mouseMoved() {
		logic.moved();
	}
	
	@Override 
	public void mouseClicked() {
		logic.clicked();
	}
	
	@Override
	public void mouseReleased() {
		logic.released();
	}
	
	public void mouseDragged () {
		logic.dragged();
	}
	public void keyPressed() {
		logic.kpress();
	}
	

}
