package process;

/**
 * @author cary.shi on 2019/12/5
 */
public class ProcessExecutorFile {
    private Process process;
    private String outFilePath;
    private String errorFilePath;

    public ProcessExecutorFile(Process process, String outFilePath, String errorFilePath) {
        this.process = process;
        this.outFilePath = outFilePath;
        this.errorFilePath = errorFilePath;
    }


    public int execute() {
        int res = 0;
        ProcessOutputThreadFile outputThread = new ProcessOutputThreadFile(this.process.getInputStream(), this.outFilePath);
        ProcessOutputThreadFile errorThread = new ProcessOutputThreadFile(this.process.getErrorStream(), this.errorFilePath);
        outputThread.start();
        errorThread.start();
        try {
            res = this.process.waitFor();
            outputThread.join();
            errorThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return res;
    }
}
