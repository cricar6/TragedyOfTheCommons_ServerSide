
import processing.core.PApplet;

public class MainApp extends PApplet {

	private Logic logic;

	public static void main(String[] args) {
		PApplet.main("MainApp");
	}

	@Override
	public void settings() {
		size(1200, 700);
	}

	@Override
	public void setup() {

		logic = new Logic(this);
	}

	@Override
	public void draw() {
		colorMode(HSB,360,100,100);
		background (208,3,85);
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

	public void mouseDragged() {
		logic.dragged();
	}

	public void keyPressed() {
		logic.kpress();
	}
}
