/*
 *  Team Name: monday-16-15-team-04
 *  Team Member:
 *               Xinyi Yuan
 *               He Shen
 *               Yuchen Dong
 */

package src.matachi.mapeditor.editor.LoadStrategy;

import java.io.File;

public class LoadContext {
    private LoadStrategy strategy;

    public void setStrategy(LoadStrategy strategy) {
        this.strategy = strategy;
    }

    public void load(File selectedFile) {
        strategy.load(selectedFile);
    }
}
