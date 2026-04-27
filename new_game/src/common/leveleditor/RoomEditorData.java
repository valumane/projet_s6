package common.leveleditor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RoomEditorData implements Serializable {
    private static final long serialVersionUID = 1L;

	private String name;
    private String description;
    private int gridX;
    private int gridY;
    private boolean isStartRoom;
    private boolean isBossRoom;

    // directions : north, south, east, west
    //la valeur est l'index de la RoomEditorData dans LevelData.rooms
    private final List<String> exitDirections = new ArrayList<>();
    private final List<Integer> exitTargetIndices = new ArrayList<>();
    private final List<Boolean> exitLocked = new ArrayList<>();

    private final List<String> itemDescriptors = new ArrayList<>();

    private final List<String> enemyDescriptors = new ArrayList<>();
    
    
    // getter et setters par eclipse
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getGridX() {
		return gridX;
	}

	public void setGridX(int gridX) {
		this.gridX = gridX;
	}

	public int getGridY() {
		return gridY;
	}

	public void setGridY(int gridY) {
		this.gridY = gridY;
	}

	public boolean isStartRoom() {
		return isStartRoom;
	}

	public void setStartRoom(boolean isStartRoom) {
		this.isStartRoom = isStartRoom;
	}

	public boolean isBossRoom() {
		return isBossRoom;
	}

	public void setBossRoom(boolean isBossRoom) {
		this.isBossRoom = isBossRoom;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<String> getExitDirections() {
		return exitDirections;
	}

	public List<Integer> getExitTargetIndices() {
		return exitTargetIndices;
	}

	public List<Boolean> getExitLocked() {
		return exitLocked;
	}

	public List<String> getItemDescriptors() {
		return itemDescriptors;
	}

	public List<String> getEnemyDescriptors() {
		return enemyDescriptors;
	}
}