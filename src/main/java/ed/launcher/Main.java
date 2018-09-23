package ed.launcher;

public class Main {

    public static void main(String args[]) {
        EDUpdater edUpdater = new EDUpdater();
        try {
            edUpdater.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
