package common.leveleditor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LevelData implements Serializable {
    private static final long serialVersionUID = 1L;

	private String levelName;
    private String levelDescription;
    private List<RoomEditorData> rooms = new ArrayList<>();

    // index de la room de départ
    private int startRoomIndex = 0;

    // autres données pour le fun
    private long creationDate;
    private long lastModifiedDate;

    public RoomEditorData getStartRoom() {
        return rooms.get(startRoomIndex);
    }
    
    // getter et setters par eclipse
    public String getLevelName() {
		return levelName;
	}

	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}

	public String getLevelDescription() {
		return levelDescription;
	}

	public void setLevelDescription(String levelDescription) {
		this.levelDescription = levelDescription;
	}

	public List<RoomEditorData> getRooms() {
		return rooms;
	}

	public void setRooms(List<RoomEditorData> rooms) {
		this.rooms = rooms;
	}

	public int getStartRoomIndex() {
		return startRoomIndex;
	}

	public void setStartRoomIndex(int startRoomIndex) {
		this.startRoomIndex = startRoomIndex;
	}

	public long getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}

	public long getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(long lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}