import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.izhonghong.plugin.idea.warhelper.ui.WarHelperDialog;

public class WarHelperAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        WarHelperDialog.init(e.getProject());
    }
}
