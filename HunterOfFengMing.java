import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

abstract class Hunter {
    public String name;                   // 监管者名称
    public double skillOfCD;              // 技能冷却时间
    public double speed;                  // 移动速度
    public String skill;                  // 技能
    public String supportSkill1;          // 辅助技能1
    public String supportSkill2;          // 辅助技能2
    public double shaveTime;              // 擦刀时间
    public double lengthOfStrike;         // 普攻刀气
    public double lengthOfPowerfulStrike; // 蓄力刀刀气

    public Hunter(String name, double skillOfCD, double speed,
                  String skill, String supportSkill1, String supportSkill2, double shaveTime,
                  double lengthOfStrike, double lengthOfPowerfulStrike) {
        this.name = name;
        this.skillOfCD = skillOfCD;
        this.speed = speed;
        this.skill = skill;
        this.supportSkill1 = supportSkill1;
        this.supportSkill2 = supportSkill2;
        this.shaveTime = shaveTime;
        this.lengthOfStrike = lengthOfStrike;
        this.lengthOfPowerfulStrike = lengthOfPowerfulStrike;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getSkillOfCD() { return skillOfCD; }
    public void setSkillOfCD(double skillOfCD) { this.skillOfCD = skillOfCD; }
    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = speed; }
    public String getSkill() { return skill; }
    public void setSkill(String skill) { this.skill = skill; }
    public String getSupportSkill1() { return supportSkill1; }
    public void setSupportSkill1(String supportSkill1) { this.supportSkill1 = supportSkill1; }
    public String getSupportSkill2() { return supportSkill2; }
    public void setSupportSkill2(String supportSkill2) { this.supportSkill2 = supportSkill2; }
    public double getShaveTime() { return shaveTime; }
    public void setShaveTime(double shaveTime) { this.shaveTime = shaveTime; }
    public double getLengthOfStrike() { return lengthOfStrike; }
    public void setLengthOfStrike(double lengthOfStrike) { this.lengthOfStrike = lengthOfStrike; }
    public double getLengthOfPowerfulStrike() { return lengthOfPowerfulStrike; }
    public void setLengthOfPowerfulStrike(double lengthOfPowerfulStrike) { this.lengthOfPowerfulStrike = lengthOfPowerfulStrike; }

    @Override
    public String toString() {
        return "Hunter{" +
                "name='" + name + '\'' +
                ", skillOfCD=" + skillOfCD +
                ", speed=" + speed +
                ", skill='" + skill + '\'' +
                ", supportSkill1='" + supportSkill1 + '\'' +
                ", supportSkill2='" + supportSkill2 + '\'' +
                ", shaveTime=" + shaveTime +
                ", lengthOfStrike=" + lengthOfStrike +
                ", lengthOfPowerfulStrike=" + lengthOfPowerfulStrike +
                '}';
    }
}

// 攻击行为接口
interface STRIKE {
    // 平A函数
    void ordinaryStrike() throws InterruptedException;
    // 技能攻击函数
    void skillStrike();
}

// 凤鸣监管者实现类
class FengMing extends Hunter implements STRIKE {
   
    private static final double POWERFUL_SHAVE_TIME_RATIO = 1.2; // 蓄力刀擦刀时间倍率
    private static final double AFTER_SHAKE_TIME = 2012; // 未普攻的后摇时间（毫秒）
    // 技能相关常量
    private static final int MAX_SYNCHRONIZED_SKILL = 2; // 同时最多使用2个技能
    private static final int PHOENIX_SKILL_0 = 1; // 零阶凤鸣数量
    private static final int PHOENIX_SKILL_1 = 2; // 一阶凤鸣数量
    private static final int PHOENIX_SKILL_2 = 4; // 二阶凤鸣数量
    private static final double SKILL_CD_0 = 12; // 零阶技能冷却（秒）
    private static final double SKILL_CD_1 = 12; // 一阶技能冷却（秒）
    private static final double SKILL_CD_2 = 10; // 二阶技能冷却（秒）
    private static final double SPEED_RATIO_1 = 1.25; // 一阶移速倍率
    private static final double SPEED_RATIO_2 = 3.15; // 二阶疾跑移速倍率
    private static final double RUN_DURATION_2 = 8; // 二阶疾跑持续时间（秒）
    private static final double RUN_CD_2 = 5; // 二阶疾跑冷却（秒）

    public FengMing(String name, double skillOfCD, double speed,
                    String skill, String supportSkill1, String supportSkill2, double shaveTime,
                    double lengthOfStrike, double lengthOfPowerfulStrike) {
        super(name, skillOfCD, speed, skill, supportSkill1, supportSkill2, shaveTime, lengthOfStrike, lengthOfPowerfulStrike);
    }

    // 平A实现
    @Override
    public void ordinaryStrike() throws InterruptedException {
        Scanner scanner = new Scanner(System.in); 
        try {
            System.out.println("是否普攻（true/false）：");
            boolean isStrike = scanner.nextBoolean();

            if (isStrike) {
                System.out.println("普攻是否有效（true/false）：");
                boolean isEffective = scanner.nextBoolean();

                if (isEffective) {
                    System.out.println("是否使用蓄力刀（true/false）：");
                    boolean powerfulStrike = scanner.nextBoolean();

                    double actualShaveTime = powerfulStrike ? this.shaveTime * 1000 * POWERFUL_SHAVE_TIME_RATIO : this.shaveTime * 1000;
                    String strikeType = powerfulStrike ? "蓄力刀" : "普通普攻";

                    // 单个线程池处理擦刀，使用后关闭
                    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
                    Runnable strikeTask = () -> System.out.println(strikeType + "擦刀时间中，剩余" + actualShaveTime / 1000 + "秒");
                    executor.schedule(strikeTask, (long) (actualShaveTime / 1000), TimeUnit.SECONDS);
                    executor.shutdown(); 
                }
            } else {
                System.out.println("未普攻，后摇结束中……");
                Thread.sleep((long) AFTER_SHAKE_TIME); // 2.012秒后摇
            }
        } catch (InputMismatchException e) {
            System.out.println("输入错误！请输入true或false。");
        } finally {
            scanner.close(); 
        }
    }
//实现技能杀
    @Override
    public void skillStrike() {
        Scanner scanner = new Scanner(System.in); // 局部Scanner
        ExecutorService skillExecutor = Executors.newFixedThreadPool(MAX_SYNCHRONIZED_SKILL); // 技能线程池

        try {
            System.out.println("请输入当前监管者的阶数（0/1/2）：");
            int tie = scanner.nextInt();

            if (tie < 0 || tie > 2) {
                throw new IllegalArgumentException("阶数只能是0、1或2！");
            }

            // 初始化凤鸣技能数量
            AtomicInteger phoenixSkillCount = new AtomicInteger();
            double skillCD = 0;
            double speedRatio = 1.0;

            // 根据阶数设置参数
            switch (tie) {
                case 0:
                    phoenixSkillCount.set(PHOENIX_SKILL_0);
                    skillCD = SKILL_CD_0;
                    break;
                case 1:
                    phoenixSkillCount.set(PHOENIX_SKILL_1);
                    skillCD = SKILL_CD_1;
                    speedRatio = SPEED_RATIO_1;
                    System.out.println("一阶效果：移速提升至原速的" + SPEED_RATIO_1 + "倍");
                    break;
                case 2:
                    phoenixSkillCount.set(PHOENIX_SKILL_2);
                    skillCD = SKILL_CD_2;
                    speedRatio = SPEED_RATIO_2;
                    System.out.println("二阶效果：移速提升至原速的" + SPEED_RATIO_2 + "倍（疾跑持续" + RUN_DURATION_2 + "秒，冷却" + RUN_CD_2 + "秒）");
                    break;
            }

            // 技能使用逻辑
            while (phoenixSkillCount.get() > 0) {
                System.out.println("是否使用凤鸣技能（true/false）？剩余数量：" + phoenixSkillCount.get());
                boolean useSkill = scanner.nextBoolean();

                if (useSkill) {
                    int remaining = phoenixSkillCount.decrementAndGet(); // 原子自减（仅调用一次）
                    skillExecutor.submit(() -> {
                        try {
                            System.out.println("使用了凤鸣技能！剩余数量：" + remaining);
                            System.out.println("技能冷却中，剩余" + skillCD + "秒");
                            Thread.sleep((long) (skillCD * 1000)); // 冷却时间
                            System.out.println("凤鸣技能冷却完成！");
                        } catch (InterruptedException e) {
                            System.out.println("技能线程被中断：" + e.getMessage());
                        }
                    });
                } else {
                    System.out.println("未使用凤鸣技能，退出技能释放流程");
                    break;
                }
            }

            if (phoenixSkillCount.get() == 0) {
                System.out.println("凤鸣技能已用完！");
            }

        } catch (InputMismatchException e) {
            System.out.println("输入错误！请输入数字（0/1/2）。");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } finally {
            skillExecutor.shutdown();
            try {
                if (!skillExecutor.awaitTermination(1, TimeUnit.SECONDS)) {
                    skillExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                skillExecutor.shutdownNow();
            }
            scanner.close();
        }
    }
}

public class HunterOfFengMing {
    public static void main(String[] args) {
        try {
            // 初始化凤鸣监管者（参数：名称、初始冷却、移速、主技能、辅助技能1、辅助技能2、擦刀时间、普攻刀气、蓄力刀刀气）
            FengMing fengMing = new FengMing("凤鸣", 6, 5.43, "伪装杀", "远程移动障碍物", "赋能移动障碍物", 4.34, 3.55, 3.67);
            System.out.println("监管者信息：" + fengMing);

            // 执行普攻和技能
            fengMing.ordinaryStrike();
            fengMing.skillStrike();
        } catch (InterruptedException e) {
            System.out.println("程序被中断：" + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
