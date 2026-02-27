package map.model;

public class BossRoomModel extends RoomModel {

    private final String bossName;

    private boolean bossDefeated;

    public BossRoomModel(String p_name, String p_description, String p_bossName) {
        super(p_name, p_description);
        this.bossName = p_bossName;
        this.bossDefeated = false;
    }

    public String getBossName() {
        return this.bossName;
    }

    public boolean isBossDefeated() {
        return this.bossDefeated;
    }

    public void defeatBoss() {
        this.bossDefeated = true;
    }
}