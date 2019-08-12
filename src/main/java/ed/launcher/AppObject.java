package ed.launcher;

public class AppObject {

    private int index;
    private String name;
    private String exec;
    private String remoteTextPath;
    private String initialPath;
    private String localPath;
    private boolean updateAvailable = false;

    public AppObject(int index, String name, String exec, String remoteTextPath, String initialPath, String localPath) {
        this.index = index;
        this.name = name;
        this.exec = exec;
        this.remoteTextPath = remoteTextPath;
        this.initialPath = initialPath;
        this.localPath = localPath;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String getRemoteTextPath() {
        return remoteTextPath;
    }

    public String getInitialPath() {
        return initialPath;
    }

    public String getLocalPath() {
        return localPath;
    }

    public String getExec() {
        return exec;
    }

    public boolean isUpdateAvailable() {
        return updateAvailable;
    }

    public void setUpdateAvailable(boolean updateAvailable) {
        this.updateAvailable = updateAvailable;
    }
}
