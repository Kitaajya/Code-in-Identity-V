//求生者大类

import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

    public Survivor(String name,
            String skill,
            String gift,
            double time,
            boolean isStrike,
            double healthValue
    ) {
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

class Mercenary extends Survivor {

    public Mercenary() {
        super("佣兵", "弹护腕", "双弹飞轮", 8, true, 1);
    }

    public static void mercenarySelf() {

        Mercenary mercenary = new Mercenary();
        mercenary.healthValueTest();        //通过对象调用healthValueTest()函数
        System.out.println(mercenary);
    }

    @Override
    public void healthValueTest() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("你是否受伤了？");
            isStrike = scanner.nextBoolean();
            if (isStrike) {
                ScheduledThreadPoolExecutor scheduledThreadPoll = new ScheduledThreadPoolExecutor(1);

                double totalHealth = 1.0;
                double strikedHealthValue = 0.5;        //假设受击一次掉0.5血量
                //====================================预留==================================================
                while (true) {

                    try (Scanner scanner1 = new Scanner(System.in)) {
                        System.out.println("请输入受击次数：");
                        int i = scanner1.nextInt();
                        if (i >= 3) {                       //攻击次数
                            Runnable task = ()
                                    -> System.out.println("受击三次，直接倒地！");
                            scheduledThreadPoll.schedule(task, 0, TimeUnit.SECONDS);
                            double reverseHealthValue = totalHealth - i * strikedHealthValue;           //剩余血量
                            if (reverseHealthValue < 0) {
                                reverseHealthValue = 0;
                            }
                            System.out.println("你的血量为" + reverseHealthValue);
                            scheduledThreadPoll.close();
                            //break;
                        } else {
                            Runnable task = ()
                                    -> System.out.println("3秒后结算延迟伤害！");                      //假设佣兵三秒后伤害结算
                            scheduledThreadPoll.schedule(task, 3, TimeUnit.SECONDS);
                            //设计倒计时
                            for (int j = 0; j <= 3; j++) {
                                int totalTime = 4;
                                int r_time = totalTime - j;
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException ex) {
                                    System.getLogger(Mercenary.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                                }
                                System.out.print(r_time + "秒后结算伤害\r");
                            }
                            System.out.println("你受到了攻击！");
                            scheduledThreadPoll.close();
                        }
                        break;
                    }

                }

            } else {
                System.out.println("你没有受到伤害！");
            }
        }
    }

    @Override
    public String toString() {
        return "姓名：" + this.name
                + "\n技能：" + this.skill + "\n天赋：" + this.gift;
    }

}

public class testMercenary {

    public static void main(String[] args) {
        Mercenary.mercenarySelf();
    }
}
