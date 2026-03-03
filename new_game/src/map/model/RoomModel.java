package map.model;

import mvc.Model;

import java.util.HashMap;
import java.util.Map;

public abstract class RoomModel implements Model {

	private final String name;

	private final String description;

	private final Map<String, Exit> exits;

	protected RoomModel(String p_name, String p_description) {
		this.name = p_name;
		this.description = p_description;
		this.exits = new HashMap<>();
	}

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}

	public Map<String, Exit> getExits() {
		return this.exits;
	}

	public Exit getExit(String p_direction) {
		return this.exits.get(p_direction.toLowerCase());
	}

	public void addExit(String p_direction, Exit p_exit) {
		this.exits.put(p_direction.toLowerCase(), p_exit);
	}

	@Override
	public void run() {
		// No specific behaviour at model level
	}
}