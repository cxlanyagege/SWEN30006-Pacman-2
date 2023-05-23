package src.matachi.mapeditor.editor;

import java.util.ArrayList;
import java.util.List;

class FolderComponent implements Component {
    private List<Component> components;

    public FolderComponent() {
        this.components = new ArrayList<>();
    }

    public void add(Component component) {
        components.add(component);
    }

    public void remove(Component component) {
        components.remove(component);
    }

    public void save() {
        for (Component component : components) {
            component.save();
        }
    }

    public void load() {
        for (Component component : components) {
            component.load();
        }
    }
}
