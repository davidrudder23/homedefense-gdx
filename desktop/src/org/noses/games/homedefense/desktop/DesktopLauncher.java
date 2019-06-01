package org.noses.games.homedefense.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.geolocation.IPAddressGeolocator;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1024;
		config.height = 768;

		// TODO Get api key from props
		new LwjglApplication(new HomeDefenseGame(new IPAddressGeolocator("00a4da2c55a1d6b04c9dc8abe8a9474d")), config);
	}
}
