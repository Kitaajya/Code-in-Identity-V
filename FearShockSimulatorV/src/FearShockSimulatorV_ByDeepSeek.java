import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

class Simulator_DeepSeek {
    protected double distance;
    protected boolean strike;
    protected int healthValue = 100; // 默认100，受击一次扣50，两次倒地

    public double getDistance() { return distance; }
    public boolean isStruck() { return strike; }
    public int getHealthValue() { return healthValue; }

    public void setDistance(double distance) { this.distance = distance; }
    public void setStrike(boolean strike) { this.strike = strike; }
    public void setHealthValue(int healthValue) { this.healthValue = healthValue; }

    public Simulator_DeepSeek() {
        this.distance = 0.0;
        this.strike = false;
        this.healthValue = 100;
    }

    public void println(Object o) { System.out.println(o); }
    public void print(Object o) { System.out.print(o); }
    public void println() { System.out.println(); }
}

class SimulatorDeepSeekChild extends Simulator_DeepSeek {
    private Scanner scanner = new Scanner(System.in);

    // 游戏总时间12秒
    public final long GAME_TOTAL_TIME = 12000L;
    // 求生者交互后的恐惧震慑判定时间段 Δt = 2秒
    public final long FEAR_SHOCK_WINDOW = 2000L;
    // 监管者攻击范围
    public static final double STRIKE_RANGE = 3.54;

    // 记录游戏开始时间（相对时间基准）
    private long gameStartTime;

    // 事件时间记录（相对于游戏开始的时间戳）
    private AtomicLong survivorInteractionTime = new AtomicLong(-1);
    private AtomicLong hunterStrikeTime = new AtomicLong(-1);

    // 事件标志
    private volatile boolean survivorInteracted = false;
    private volatile boolean hunterStruck = false;
    private volatile boolean gameRunning = true;

    // 恐惧震慑触发标志
    private volatile boolean fearShockTriggered = false;

    public void println(Object o) { System.out.println(o); }
    public void print(Object o) { System.out.print(o); }
    public void println() { System.out.println(); }

    /**
     * 游戏主控制器
     */
    public void runGame() {
        println("========== 第五人格恐惧震慑模拟器 V ==========");
        println("游戏规则：");
        println("1. 游戏总时长12秒");
        println("2. 求生者按 Q 键进行交互");
        println("3. 监管者按 E 键进行攻击");
        println("4. 如果监管者在求生者交互后2秒内击中求生者，触发恐惧震慑");
        println("5. 恐惧震慑造成双倍伤害（直接扣除100点生命值）");
        println("==========================================");

        // 记录游戏开始时间
        gameStartTime = System.currentTimeMillis();

        // 创建并启动三个计时器线程
        Thread gameTimerThread = new Thread(this::gameTimer, "游戏计时器");
        Thread survivorThread = new Thread(this::survivorAction, "求生者线程");
        Thread hunterThread = new Thread(this::hunterAction, "监管者线程");
        Thread judgeThread = new Thread(this::judgeFearShock, "判定线程");

        gameTimerThread.start();
        survivorThread.start();
        hunterThread.start();
        judgeThread.start();

        // 等待游戏结束
        try {
            gameTimerThread.join();
        } catch (InterruptedException e) {
            println("游戏主线程被中断");
        }

        gameRunning = false;
        println("\n========== 游戏结束 ==========");
        println("最终状态：");
        println("求生者生命值: " + healthValue);
        println("恐惧震慑触发: " + (fearShockTriggered ? "是" : "否"));
    }

    /**
     * 游戏计时器线程
     */
    private void gameTimer() {
        try {
            for (int i = 0; i <= GAME_TOTAL_TIME / 1000 && gameRunning; i++) {
                Thread.sleep(1000);
                print("\r游戏进行中... " + i + " 秒");
            }
        } catch (InterruptedException e) {
            println("游戏计时器中断");
        }
        println("\n游戏时间结束！");
    }

    /**
     * 求生者行为线程
     */
    private void survivorAction() {
        println("求生者已就绪，请在适当时机按 Q 键交互...");

        while (gameRunning && !survivorInteracted) {
            if (scanner.hasNext()) {
                String input = scanner.next();
                if ("Q".equalsIgnoreCase(input)) {
                    long currentTime = System.currentTimeMillis() - gameStartTime;
                    if (currentTime <= GAME_TOTAL_TIME) {
                        survivorInteractionTime.set(currentTime);
                        survivorInteracted = true;
                        println("\n>>> 求生者在 " + (currentTime / 1000.0) + " 秒时进行交互！");

                        // 模拟交互后的恐惧震慑判定窗口
                        println(">>> 恐惧震慑判定窗口开启（持续2秒）...");
                    } else {
                        println("游戏已结束，无法交互！");
                    }
                    break;
                }
            }
        }
    }

    /**
     * 监管者行为线程
     */
    private void hunterAction() {
        println("监管者已就绪，请在适当时机按 E 键攻击...");

        while (gameRunning && !hunterStruck) {
            if (scanner.hasNext()) {
                String input = scanner.next();
                if ("E".equalsIgnoreCase(input)) {
                    long currentTime = System.currentTimeMillis() - gameStartTime;
                    if (currentTime <= GAME_TOTAL_TIME) {
                        // 模拟攻击距离判定
                        println("\n请输入监管者与求生者的距离（米）：");
                        try {
                            double distance = scanner.nextDouble();
                            if (distance <= STRIKE_RANGE) {
                                hunterStrikeTime.set(currentTime);
                                hunterStruck = true;
                                println(">>> 监管者在 " + (currentTime / 1000.0) + " 秒时成功击中求生者！");
                            } else {
                                println(">>> 攻击距离过远（" + distance + "米 > " + STRIKE_RANGE + "米），攻击无效！");
                            }
                        } catch (Exception e) {
                            println("输入错误，攻击失败！");
                            scanner.nextLine(); // 清除错误输入
                        }
                    } else {
                        println("游戏已结束，无法攻击！");
                    }
                    break;
                }
            }
        }
    }

    /**
     * 恐惧震慑判定线程
     */
    private void judgeFearShock() {
        while (gameRunning && !fearShockTriggered) {
            // 检查是否同时有交互和攻击事件发生
            if (survivorInteracted && hunterStruck) {
                long interactionTime = survivorInteractionTime.get();
                long strikeTime = hunterStrikeTime.get();

                // 判定是否在恐惧震慑窗口内
                if (strikeTime >= interactionTime &&
                        strikeTime <= interactionTime + FEAR_SHOCK_WINDOW) {

                    fearShockTriggered = true;
                    println("\n========== 恐惧震慑触发！ ==========");
                    println("求生者交互时间: " + (interactionTime / 1000.0) + " 秒");
                    println("监管者攻击时间: " + (strikeTime / 1000.0) + " 秒");
                    println("时间差: " + ((strikeTime - interactionTime) / 1000.0) + " 秒");
                    println("恐惧震慑造成双倍伤害！");

                    // 造成双倍伤害
                    healthValue -= 100;
                    println("求生者当前生命值: " + healthValue);

                    if (healthValue <= 0) {
                        println("求生者倒地！");
                    }
                } else {
                    println("\n>>> 攻击发生在恐惧震慑窗口外，造成普通伤害");
                    healthValue -= 50;
                    println("求生者当前生命值: " + healthValue);
                }
                break;
            }

            // 短暂休眠避免忙等待
            LockSupport.parkNanos(1000000); // 1ms
        }
    }

    /**
     * 简单的测试方法（用于兼容原有代码）
     */
    public void test() {
        runGame();
    }
}

public class FearShockSimulatorV_ByDeepSeek {
    public static void main(String[] args) {
        SimulatorDeepSeekChild simulator = new SimulatorDeepSeekChild();
        simulator.runGame();
    }
}