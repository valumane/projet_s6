package mvc.map.view.base;

import mvc.mvc.View;

public abstract class ExitView implements View {

    @Override
    public void hide() {
    }

    @Override
    public void show() {
    }

    public abstract void displayLockedDoor();
    
    public abstract void displayUnlockedDoor();
    
    public abstract void displayNeedKey(String keyName);
}
