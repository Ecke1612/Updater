package ed.launcher;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Processer {

    private Process process;
    private Thread threadOut;
    private Thread threadErr;

    public void startJar(String jarFile, String os) throws InterruptedException, IOException {
        if (os.equals("windows")) System.out.println("start on Windows");
        else if (os.equals("linux")) System.out.println("start on Linux");

        List alist = new ArrayList<>();

        // add the list of commands to the list
        if (os.equals("linux")) alist.add("sudo");
        alist.add("java");
        if (os.equals("linux")) alist.add("-Djavafx.platform=gtk2");
        alist.add("-jar");
        alist.add(jarFile);

        // initialize the processbuilder
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(alist);
        try {
            // start the process
            process = builder.start();
            consume();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void destroyProcess() throws InterruptedException {
        process.destroy();
        threadOut.join();
        threadErr.join();
    }

    public void consume() {
        InputStream stdout = process.getInputStream();
        InputStream stderr = process.getErrorStream();

        threadOut = new Thread(new MyInputStreamSink(stdout, "out"));
        threadErr = new Thread(new MyInputStreamSink(stderr, "err"));

        threadOut.setDaemon(true);
        threadErr.setDaemon(true);
        threadOut.setName(String.format("stdout reader"));
        threadErr.setName(String.format("stderr reader"));

        threadOut.start();
        threadErr.start();
    }

    public static class MyInputStreamSink implements Runnable {
        private InputStream m_in;
        private String m_streamName;

        MyInputStreamSink(InputStream in, String streamName) {
            m_in = in;
            m_streamName = streamName;
        }

        @Override
        public void run() {
            BufferedReader reader = null;
            Writer writer = null;

            try {
                reader = new BufferedReader(new InputStreamReader(m_in));

                for (String line = null; ((line = reader.readLine()) != null); ) {
                    // TODO: Do something with the output, maybe.
                    System.out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (null != reader) reader.close();
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
