package process;

import config.PathConfig;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.NullOutputStream;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Common utils
 *
 * @author cary.shi on 2019/10/11
 */
public class ProcessUtils {
    /**
     * Process the stream generated in process to /dev/null
     */
    public static void processMessageToNull(final InputStream inputStream) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Reader reader = new InputStreamReader(inputStream);
                BufferedReader br = new BufferedReader(reader);
                // write to /dev/null
                NullOutputStream nullOutputStream = new NullOutputStream();
                try {
                    String line;
                    while ((line = br.readLine()) != null) {
                        nullOutputStream.write(line.getBytes(StandardCharsets.UTF_8));
                    }
                    nullOutputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    System.err.println("print processMessage Error");
                }
            }
        }).start();
    }

    /**
     * Process the stream generated in process to String
     */
    public static List<String> processMessageToString(InputStream inputStream) {
        List<String> lines = new CopyOnWriteArrayList<>();
        new Thread(() -> {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    lines.add(line);
                }
                bufferedReader.close();
            } catch (IOException ignored) {
            }
        }).start();

        return lines;
    }

    public static void processMessageToFile(InputStream inputStream, File file) {
        new Thread(() -> {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    FileUtils.write(file, line, StandardCharsets.UTF_8, true);
                    FileUtils.write(file, System.getProperty("line.separator"), StandardCharsets.UTF_8, true);
                }
                bufferedReader.close();
                inputStream.close();
            } catch (IOException ignored) {
            }
        }).start();
    }

    public static void processMessageToFileLocal(InputStream inputStream, File file) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                FileUtils.write(file, line, StandardCharsets.UTF_8, true);
                FileUtils.write(file, System.getProperty("line.separator"), StandardCharsets.UTF_8, true);
            }
            bufferedReader.close();
            inputStream.close();
        } catch (IOException ignored) {
        }
    }

}
