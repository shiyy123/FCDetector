package process;

import java.util.List;

/**
 * @author cary.shi on 2019/12/5
 */
public class ProcessExecutorList {
    private Process process;
    private List<String> outputList;
    private List<String> errorList;

    public ProcessExecutorList(Process process) {
        this.process = process;
    }

    public List<String> getOutputList() {
        return this.outputList;
    }

    public List<String> getErrorList() {
        return this.errorList;
    }

    public int execute() {
        int res = 0;
        ProcessOutputThread outputThread = new ProcessOutputThread(this.process.getInputStream());
        ProcessOutputThread errorThread = new ProcessOutputThread(this.process.getErrorStream());
        outputThread.start();
        errorThread.start();
        try {
            res = this.process.waitFor();
            outputThread.join();
            errorThread.join();
            this.outputList = outputThread.getOutputList();
            this.errorList = errorThread.getOutputList();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return res;
    }

}
