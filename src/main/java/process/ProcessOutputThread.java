package process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cary.shi on 2019/12/5
 */
public class ProcessOutputThread extends Thread {
    private InputStream is;
    private List<String> outputList;

    ProcessOutputThread(InputStream is) {
        this.is = is;
        this.outputList = new ArrayList<>();
    }

    List<String> getOutputList() {
        return this.outputList;
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
//                DebugUtils.debug(output);
                this.outputList.add(output);
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