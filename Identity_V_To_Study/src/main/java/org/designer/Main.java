package org.designer;

import java.util.Scanner;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class Console {
    public static void Write(Object obj) {
        System.out.print(obj);
    }

    public static void WriteLine(Object obj) {
        System.out.println(obj);
    }
}

// 破译密码机类
class DecodeMachine {
    Scanner DECODE = new Scanner(System.in);
    int[][] machine = new int[6][1];

    private boolean isWaiting = false;
    private volatile boolean isBlocked = false;

    // 封锁密码机（供监管者调用）
    public void blockMachine() {
        isBlocked = true;
        Console.WriteLine("\n这台密码机无法破译！");
    }

    // 解封密码机
    public void unblockMachine() {
        isBlocked = false;
        synchronized (this) {
            this.notifyAll();  // 唤醒所有等待的修机线程
        }
        Console.WriteLine("密码机已解封！");
    }

    public synchronized void decode() throws InterruptedException {
        for (int i = 0; i < machine.length; i++) {
            for (int j = 0; j < machine[i].length; j++) {
                while (machine[i][j] < 100) {
                    // 检查是否被封锁
                    while (isBlocked) {
                        Console.WriteLine("\n密码机被封锁，等待解封...");
                        this.wait();  // 被封锁时等待
                    }

                    machine[i][j]++;
                    Console.Write("\r密码机" + i + "进度：" + machine[i][j] + "%");

                    if (machine[i][j] == 99&&i==5) justify();
                    Thread.sleep(20);
                }
                Console.WriteLine("\n密码机" + i + "破译完成！总进度：" + machine[i][j] + "%");
            }
        }
        Console.WriteLine("\n所有密码机破译完成");
    }

    synchronized void justify() throws InterruptedException {
        Console.WriteLine("\n压好密码机了！");
        isWaiting = true;

        Console.Write("是否继续修机？(Y/N): ");
        String ISDECODE = DECODE.next();

        if (ISDECODE.equalsIgnoreCase("Y")) {
            Console.WriteLine("继续破译...");
            isWaiting = false;
        } else {
            Console.WriteLine("停止破译，等待队友来压机...");
            // 设置超时，避免永久等待
            this.wait(100);  // 等待0.1秒后自动继续
            isWaiting = false;
        }
    }

    void concurrency() {
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        Runnable r = () -> {
            try {
                decode();
            } catch (InterruptedException ex) {
                Console.WriteLine("修机进度发生异常！->decode()" + ex.getMessage());
                Thread.currentThread().interrupt();
            }
        };
        scheduledThreadPoolExecutor.submit(r);
        scheduledThreadPoolExecutor.shutdown();
        try {
            scheduledThreadPoolExecutor.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

class BlockMachine {
    private Thread decodeThread;
    private Thread blockThread;
    private DecodeMachine decodeMachine;

    void blockMachines() {
        decodeMachine = new DecodeMachine();
        Object lock = new Object();

        Runnable decodeTask = () -> {
            try {
                decodeMachine.concurrency();
            } catch (Exception e) {
                Console.WriteLine("破译过程出错: " + e.getMessage());
            }
        };

        Runnable blockTask = () -> {
            synchronized (lock) {
                try {
                    decodeMachine.blockMachine();        // 调用封锁方法
                    Thread.sleep(10000);            // 封锁持续10秒
                    decodeMachine.unblockMachine();     // 自动解封
                } catch (InterruptedException e) {
                    Console.WriteLine("封锁被中断");
                    decodeMachine.unblockMachine();      // 中断时也要解封
                    Thread.currentThread().interrupt();
                }
            }
        };

        decodeThread = new Thread(decodeTask, "DecodeThread");
        blockThread = new Thread(blockTask, "BlockThread");
    }

    void block() {
        blockMachines();
        decodeThread.start();
        try {
            Thread.sleep(1000);  // 让破译先跑1秒再封锁
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        blockThread.start();
    }

    void testBlockMachine() throws InterruptedException {
        block();
        int v = 0;
        for (; ; ) {
            v++;
            Thread.sleep(1000);
            Console.Write("\r电机解封倒计时：" + (10 - v) + "秒");
            if (v >= 10) break;
        }
        System.out.println();
    }

    static void decode() {
        DecodeMachine decodeMachine = new DecodeMachine();
        decodeMachine.concurrency();
    }
}

public class Main {
    public static void main(String[] args) throws InterruptedException {
        BlockMachine blockMachine = new BlockMachine();
        blockMachine.testBlockMachine();
    }
}