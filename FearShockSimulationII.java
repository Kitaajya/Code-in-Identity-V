/*
 * InstantStrike（第五人格抽刀调试）
 * InstantBreakPallet（小丑零帧板优先级调试）
 * 小丑火箭筒类
 * 作者：亚当斯柯南道尔
 * 时间：2025/11/13至2025/12/18
 */
// 全局唯一 Scanner（所有输入都用这个，不重复创建、不关闭）
import java.util.Scanner;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class GlobalScanner {
    public static final Scanner INSTANCE = new Scanner(System.in);
}

//<------------------------小丑武器类------------------------>
class Justice<T> {

    private T isThruster;
    private T isModifiedDrill;
    private T isWindWing;

    public Justice(T isThruster, T isModifiedDrill, T isWindWing) {
        this.isThruster = isThruster;
        this.isModifiedDrill = isModifiedDrill; // 修复原构造方法未赋值问题
        this.isWindWing = isWindWing;           // 修复原构造方法未赋值问题
    }

    public T getIsThruster() {
        return isThruster;
    }

    public T getIsModifiedDrill() {
        return isModifiedDrill;
    }

    public T getIsWindWing() {
        return isWindWing;
    }

    // 修复 print 方法：原方法只输出空行，现在正常打印消息
    public static void print(String message) {
        System.out.println(message);
    }

    public static void JustifyMain() {
        Justice<Boolean> item = new Justice<>(true, true, true);
        // 删除 try-with-resources，直接用全局 Scanner
        print("请输入你所看到的监管者安装上的道具：");
        print("你是否看到了推进器？（输入 true/false）");
        // 输入格式校验：避免用户输入非 boolean 值
        while (!GlobalScanner.INSTANCE.hasNextBoolean()) {
            print("输入错误！请重新输入 true 或 false：");
            GlobalScanner.INSTANCE.next(); // 清除无效输入
        }
        boolean isThrusterInput = GlobalScanner.INSTANCE.nextBoolean();
        if (isThrusterInput) {
            print("监管者安装了推进器！");
        }

        print("你是否看到了改装钻头？（输入 true/false）");
        while (!GlobalScanner.INSTANCE.hasNextBoolean()) {
            print("输入错误！请重新输入 true 或 false：");
            GlobalScanner.INSTANCE.next();
        }
        boolean isModifiedDrillInput = GlobalScanner.INSTANCE.nextBoolean();
        if (isModifiedDrillInput) {
            print("监管者安装了改装钻头！");
        }

        print("你是否看到了风翼？（输入 true/false）");
        while (!GlobalScanner.INSTANCE.hasNextBoolean()) {
            print("输入错误！请重新输入 true 或 false：");
            GlobalScanner.INSTANCE.next();
        }
        boolean isWindWingInput = GlobalScanner.INSTANCE.nextBoolean();
        if (isWindWingInput) {
            print("监管者安装了风翼");
        }
    }

    @Override
    public String toString() {
        return (getIsModifiedDrill() != null ? getIsModifiedDrill() : "未设置") + "\n"
                + (getIsThruster() != null ? getIsThruster() : "未设置") + "\n"
                + (getIsWindWing() != null ? getIsWindWing() : "未设置");
    }
}
//泛型类结束

class Survivor {

    protected String name;      //名字
    protected String skill;     //技能
    protected String gift;      //天赋
    protected double time;      //备用时间
    public boolean isStrike;    //是否受到攻击（默认true）
    public double healthValue;  //血量

    public String getName() {
        return name;
    }

    public String getSkill() {
        return skill;
    }

    public String getGift() {
        return gift;
    }

    public double getTime() {
        return time;
    }

    public boolean getIsStrike() {
        return isStrike;
    }

    public double getHealthValue() {
        return healthValue;
    }

    public Survivor(String name, String skill, String gift, double time, boolean isStrike, double healthValue) {
        this.name = name;
        this.skill = skill;
        this.gift = gift;
        this.time = time;
        this.isStrike = isStrike;
        this.healthValue = healthValue;
    }

    public void healthValueTest() {
        if (isStrike) {
            System.out.println("你受到了攻击！");
        } else {
            System.out.println("你没有受到攻击！");
        }
    }

    @Override
    public String toString() {
        return "姓名：" + this.name
                + "\n技能：" + this.skill + "\n天赋：" + this.gift;
    }
}

class Luckyman extends Survivor {

    public Luckyman() {
        super("幸运儿", "猜宝箱", "双弹飞轮", 9, false, 1);
    }

    public String toString() {
        return "我是" + this.name + "\n天赋是" + this.gift + "\n技能是" + this.skill;
    }

    public static void luckymanSelf() {
        Luckyman luckyman = new Luckyman();
        luckyman.healthValueTest();
        // Justice.print();
        System.out.println(luckyman);

    }

}
//佣兵类=================================开始==============================

class Mercenary extends Survivor {

    public Mercenary() {
        super("佣兵", "弹护腕", "双弹飞轮", 8, true, 1);
    }

    public static void mercenarySelf() {
        Mercenary mercenary = new Mercenary();
        mercenary.healthValueTest();
        System.out.println(mercenary);
    }

    @Override
    public void healthValueTest() {
        // 删除 try-with-resources，用全局 Scanner
        System.out.print("你是否受伤了？（输入 true/false）：");
        // 输入格式校验
        while (!GlobalScanner.INSTANCE.hasNextBoolean()) {
            System.out.print("输入错误！请重新输入 true 或 false：");
            GlobalScanner.INSTANCE.next();
        }
        isStrike = GlobalScanner.INSTANCE.nextBoolean();

        if (isStrike) {
            ScheduledThreadPoolExecutor scheduledThreadPoll = new ScheduledThreadPoolExecutor(1);
            double totalHealth = 1.0;
            double strikedHealthValue = 0.5;

            while (true) {
                // 删除 try-with-resources，用全局 Scanner
                System.out.println("请输入受击次数：（输入整数）");
                // 输入格式校验（避免非整数输入）
                while (!GlobalScanner.INSTANCE.hasNextInt()) {
                    System.out.print("输入错误！请重新输入整数：");
                    GlobalScanner.INSTANCE.next();
                }
                int i = GlobalScanner.INSTANCE.nextInt();

                if (i >= 3) {
                    Runnable task = () -> System.out.println("受击三次，直接倒地！");
                    scheduledThreadPoll.schedule(task, 0, TimeUnit.SECONDS);
                    double reverseHealthValue = totalHealth - i * strikedHealthValue;
                    reverseHealthValue = Math.max(reverseHealthValue, 0); // 血量不低于0
                    System.out.println("你的血量为：" + reverseHealthValue);
                    scheduledThreadPoll.shutdown(); // 用 shutdown() 替代 close()，更安全
                } else {
                    Runnable task = () -> System.out.println("3秒后结算延迟伤害！");
                    scheduledThreadPoll.schedule(task, 3, TimeUnit.SECONDS);

                    // 修复倒计时逻辑：从3秒倒计时到0秒
                    System.out.print("倒计时：");
                    for (int j = 3; j >= 0; j--) {
                        try {
                            Thread.sleep(1000);
                            System.out.print(j + "秒 ");
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                    System.out.println("\n你受到了攻击！");
                    scheduledThreadPoll.shutdown();
                }
                break;
            }
        } else {
            System.out.println("你没有受到伤害！");
        }
    }
//佣兵类=================================结束==============================
//幸运儿类

    @Override
    public String toString() {
        return "姓名：" + this.name
                + "\n技能：" + this.skill + "\n天赋：" + this.gift;
    }
}

class Doctor extends Survivor {

    public Doctor() {
        super("医生", "回血治疗", " ", 0.0, false, 1);
        Justice.print("请输入你的天赋：");
        GlobalScanner.INSTANCE.nextLine();
        this.gift = GlobalScanner.INSTANCE.nextLine();
        Justice.print("你选择了" + this.gift + "天赋");
    }

    @Override
    public String toString() {
        //gift = GlobalScanner.INSTANCE.nextLine();
        //Justice.print("你选择了" + this.gift + "天赋");
        return "我是" + this.name + "\n天赋是" + this.gift + "\n技能是" + this.skill;
    }

    public static void doctorSelf() {
        Doctor doctor = new Doctor();
        System.out.println(doctor);
    }
}
// 监管者动作类
class HunterBehaviour {

    public double ShaveWeaponRecoverTime;       // 擦刀恢复动作
    public double SkillRecoverTime;             // 技能恢复动作
    public double LongShaveWeaponRecoverTime;   // 蓄力刀击中后擦刀时长
    public boolean IsStrike;                    // 判断是否命中求生者

    public HunterBehaviour(double shaveWeaponRecoverTime, double skillRecoverTime, double longShaveWeaponRecoverTime, boolean isStrike) {
        this.ShaveWeaponRecoverTime = shaveWeaponRecoverTime;
        this.SkillRecoverTime = skillRecoverTime;
        this.LongShaveWeaponRecoverTime = longShaveWeaponRecoverTime;
        this.IsStrike = isStrike;
    }

    @Override
    public String toString() {
        if (IsStrike) {
            return "监管者攻击命中状态：\n"
                    + "擦刀时长：" + this.ShaveWeaponRecoverTime + "秒\n"
                    + "技能完毕恢复时长：" + this.SkillRecoverTime + "秒\n"
                    + "蓄力刀击中后擦刀时长：" + this.LongShaveWeaponRecoverTime + "秒";
        } else {
            return "监管者攻击未命中状态：\n"
                    + "擦刀时长：0秒\n"
                    + "技能完毕恢复时长：0秒\n"
                    + "蓄力刀击中后擦刀时长：0秒";
        }
    }
}

// 恐惧震慑子类
class FearShock extends HunterBehaviour {

    public double TotalTime;
    public double SurvivorTime;
    public double HunterTime;
    private boolean isRunning = true; // 控制线程运行状态

    public FearShock(double shaveWeaponRecoverTime, double skillRecoverTime, double longShaveWeaponRecoverTime, boolean isStrike, double survivorTime, double hunterTime, double totalTime) {
        super(shaveWeaponRecoverTime, skillRecoverTime, longShaveWeaponRecoverTime, isStrike);
        this.SurvivorTime = survivorTime;
        this.HunterTime = hunterTime;
        this.TotalTime = totalTime;
    }

    // 停止游戏
    public void stopGame() {
        isRunning = false;
    }

    // 检查恐惧震慑触发条件
    private void checkFearShock() {
        if (this.IsStrike) {
            if (Math.abs(this.SurvivorTime - this.TotalTime) <= 0.5) {
                System.out.println("\n恐惧震慑！");
            }
        }
    }

    public void runGame() {
        Thread gameThread = new Thread(() -> {
            try {
                while (isRunning) {
                    Thread.sleep(1000);
                    this.TotalTime += 1;
                    System.out.print("当前对局总时间：" + this.TotalTime + "秒\r");

                    // 检查恐惧震慑条件
                    checkFearShock();
                    if (this.TotalTime >= 8) {
                        stopGame();
                        System.out.print("\n对局结束！\r");
                    }
                }
            } catch (InterruptedException e) {
                System.err.println("游戏线程被中断：" + e.getMessage());
                Thread.currentThread().interrupt();
            }
        }, "GameThread");

        gameThread.start();
        System.out.println("游戏开始！");
    }

    public static void mainInformation() {
        HunterBehaviour hunter = new HunterBehaviour(4.2, 12, 4.9, true);
        System.out.println("监管者信息:");
        System.out.println(hunter);

        FearShock game = new FearShock(4.2, 12, 4.9, true, 5, 5, 0);
        System.out.println("\n恐惧震慑模拟");
        game.runGame();


        try {
            Thread.sleep(9000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void combineFunction() {
        FearShock.mainInformation();
        System.err.println("\n");
        Mercenary.mercenarySelf();
        Justice.JustifyMain();
        Luckyman.luckymanSelf();
        Doctor.doctorSelf();


        GlobalScanner.INSTANCE.close();

    }
}

// 主类
public class FearShockSimulationII {

    public static void main(String[] args) {
        FearShock.combineFunction();
    }
}
