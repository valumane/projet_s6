package map.model;

import mvc.Model;

import java.util.HashMap;
import java.util.Map;

public abstract class RoomModel implements Model {

	private final String name;

	private final String description;

	private final Map<String, ExitModel> exits;

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

	public Map<String, ExitModel> getExits() {
		return this.exits;
	}

	public ExitModel getExit(String p_direction) {
		return this.exits.get(p_direction.toLowerCase());
	}

	public void addExit(String p_direction, ExitModel p_exitModel) {
		this.exits.put(p_direction.toLowerCase(), p_exitModel);
	}

	@Override
	public void run() {
		// No specific behaviour at model level
	}
}