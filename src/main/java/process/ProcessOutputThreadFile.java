package process;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author cary.shi on 2019/12/5
 */
public class ProcessOutputThreadFile extends Thread {
    private InputStream is;
    private File file;

    ProcessOutputThreadFile(InputStream is, String filePath) {
        this.is = is;
        this.file = new File(filePath);
    }

    @Override
    public void run() {
        InputStreamReader isr = null;
        BufferedReader br = null;
        isr = new InputStreamReader(this.is);
        br = new BufferedReader(isr);
        String output = null;
        try {
            while (null != (output = br.readLine())) {
                FileUtils.write(this.file, output, StandardCharsets.UTF_8, true);
                FileUtils.write(this.file, System.getProperty("line.separator"), StandardCharsets.UTF_8, true);
            }
        } catch (IOException e) {
//            System.err.println("Process run error");
        } finally {
            try {
                br.close();
                isr.close();
                if (null != this.is) {
                    this.is.close();
                }
            } catch (IOException e) {
//                System.err.println("Process close stream error");
            }
        }
    }
}
