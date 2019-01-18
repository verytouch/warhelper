import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.izhonghong.plugin.idea.warhelper.core.DefaultWarHelper;
import com.izhonghong.plugin.idea.warhelper.core.WarHelper;
import com.izhonghong.plugin.idea.warhelper.ui.WarHelperDialog;
import com.thaiopensource.validate.StringOption;
import org.eclipse.jdt.internal.compiler.lookup.InferenceContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

public class WarHelperAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        WarHelperDialog.init(e.getProject());
    }
}
