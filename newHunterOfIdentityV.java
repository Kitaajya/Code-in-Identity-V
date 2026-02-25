import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Survivor {
    private final Random random = new Random();
    private final Scanner scanner = new Scanner(System.in);
    private final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);

    void fix() {
        for (int machineId = 1; machineId <= 3; machineId++) {
            final int taskId = machineId;
            fixedThreadPool.submit(() -> {
                try {
                    System.out.printf("\n【密码机%d】开始破译（当前线程：%s）\n",
                            taskId, Thread.currentThread().getName());
                    final int TOTAL_FIX_TIME = 6000;
                    final int PROGRESS_INTERVAL = 200;
                    int elapsedTime = 0;
                    boolean calibrateFailed = false;
                    while (elapsedTime < TOTAL_FIX_TIME && !calibrateFailed) {
                        int progress = (int) ((elapsedTime * 100.0) / TOTAL_FIX_TIME);
                        System.out.printf("\r【密码机%d】破译进度：%d%%", taskId, progress);
                        if (random.nextInt(10) == 0) {
                            calibrateFailed = handleCalibration(taskId);
                            if (calibrateFailed) {
                                break;
                            }
                        }
                        Thread.sleep(PROGRESS_INTERVAL);
                        elapsedTime += PROGRESS_INTERVAL;
                    }
                    if (calibrateFailed) {
                        System.out.printf("\n【密码机%d】校准失败，破译中断！\n", taskId);
                    } else {
                        System.out.printf("\r【密码机%d】破译进度：100%%\n", taskId);
                        System.out.printf("【密码机%d】破译完成！\n", taskId);
                    }

                } catch (InterruptedException e) {
                    System.out.printf("\n【密码机%d】破译被中断（线程异常）\n", taskId);
                    // 恢复线程中断状态
                    Thread.currentThread().interrupt();
                }
            });
        }
        fixedThreadPool.shutdown();
        scanner.close();
        System.out.println("\n所有密码机破译任务已提交，线程池已关闭");
    }
    private boolean handleCalibration(int machineId) throws InterruptedException {
        int targetNum = random.nextInt(9) + 1;
        System.out.printf("\n【密码机%d】触发校准！请输入数字%d（3秒内输入有效）\n", machineId, targetNum);
        long startTime = System.currentTimeMillis();
        final int CALIBRATE_TIMEOUT = 3000;
        while (!scanner.hasNextInt()) {
            if (System.currentTimeMillis() - startTime > CALIBRATE_TIMEOUT) {
                System.out.printf("\n【密码机%d】校准超时！\n", machineId);
                return true;
            }
            Thread.sleep(100);
        }
        int userInput = scanner.nextInt();
        scanner.nextLine();
        if (userInput != targetNum) {
            System.out.printf("\n【密码机%d】输入错误（你输入：%d，正确值：%d），校准失败！\n",
                    machineId, userInput, targetNum);
            return true;
        } else {
            System.out.printf("\n【密码机%d】校准成功！继续破译...\n", machineId);
            return false;
        }
    }
}

public class newHunterOfIdentityV {
    public static void main(String[] args) {
        Survivor s = new Survivor();
        s.fix();
    }
}
