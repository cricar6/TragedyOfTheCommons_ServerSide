package Writing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class WriteFile implements Runnable {

	private ArrayList<ArrayList<String>> players;
	private String kind;

	public WriteFile(String kind) {
		this.kind = kind;
		players = new ArrayList<ArrayList<String>>();
	}

	@Override
	public void run() {
		try {
			writeSomething();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeSomething() throws IOException {

		@SuppressWarnings("resource")
		BufferedWriter writer = new BufferedWriter(new FileWriter(kind + "Data.txt"));
		writer.newLine();

		for (int i = 0; i < players.size(); i++) {
			ArrayList<String> player = players.get(i);

			for (int j = 0; j < player.size(); j++) {
				String var = player.get(j);

				writer.write(var + ',');
			}
			writer.newLine();
		}

		writer.close();

	}

	public ArrayList<ArrayList<String>> getPlayers() {
		return players;
	}

	public void setPlayers(ArrayList<ArrayList<String>> players) {
		this.players = players;
	}

}
