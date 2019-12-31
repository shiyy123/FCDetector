package process;

import java.util.List;

/**
 * @author cary.shi on 2019/10/29
 */
public class ProcessExecutor {
    private Process process;
//    private List<String> outputList;
    private List<String> errorList;
    private String filePath;

    public ProcessExecutor(Process process, String filePath) {
        this.process = process;
        this.filePath = filePath;
    }

//    public List<String> getOutputList() {
//        return this.outputList;
//    }

    public List<String> getErrorList() {
        return this.errorList;
    }

    public int execute() {
        int res = 0;
        ProcessOutputThreadFile outputThread = new ProcessOutputThreadFile(this.process.getInputStream(), this.filePath);
        ProcessOutputThread errorThread = new ProcessOutputThread(this.process.getErrorStream());
        outputThread.start();
        errorThread.start();
        try {
            res = this.process.waitFor();
            outputThread.join();
            errorThread.join();
//            this.outputList = outputThread.getOutputList();
            this.errorList = errorThread.getOutputList();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return res;
    }

}



