/**
 * 项目>>
 *              第五次恐惧震慑模拟->[FearShockSimulatorV.java]
 * 设计思路原理>>
 *              用多线程设计计时器，根据第五人格恐惧震慑原理，整个游戏对局需要设计三个计时器（三个平行时间轴） 求生者计时器t1，监管者计时器t2，
 *              整个游戏对局计时器t3。其中，这三个计时器同时计时；不光有计时器，求生者还有被攻击参数timeOfBeStruck，监管者有攻击参数timeOfStrike，两个参数均属于时间参数。
 * 原理示例>>
 *              若某次游戏对局在t1_survivor_1时间点处求生者发生交互，而监管者并未在t1_survivor_1时间点处命中求生者（未攻击或距离远攻击无
 *              效），遂t1_survivor_1时间点上不会发生恐惧震慑。若t1_survivor_2处求生者发生交互，监管者攻击生效，则触发恐惧震慑机制。刚刚
 *              提到的t1_survivor_1和t1_survivor_2这两个名称不重要，因为t1，t2，t3时间轴是平行的。
 * 易混淆>>
 *              1.事实上是，时间轴具有平行性，攻击（生效与否）和交互的时间点不具有平行性。只有攻击生效的时间点与交互时间点重合时，则会触发恐惧震慑。
 *              2.攻击（生效与否）与交互是具有相对瞬时性的。
 *              3.当恐惧震慑触发条件具有瞬时性时，恐惧震慑不容易触发。因此，使此条件具有相对瞬时性当且仅当设置求生者交互时间段Δt1，若监管者攻击生效
 *                于Δt1，则会触发恐惧震慑。其中，Δt1代表求生者交互后的时间段。
 * 阐述相对瞬时性>>
 *              求生者在t1_survivor_x时间点处发生交互，在时间区间[t1_survivor_x,t1_survivor_x+Δt]的任意时间点内，
 *              监管者攻击生效时，恐惧震慑生效。
 * 本程序设计>>游戏对局总时间12秒，游戏第3秒监管者攻击生效，但求生者未交互；游戏对局第6秒，求生者在[t1_survivor_x,t1_survivor_x+Δt]时间段内发生
 *             交互，监管者攻击生效，触发恐惧震慑。
 *
 * **/

import java.util.InputMismatchException;
import java.util.Scanner;

class Simulator{
    public double distance;
    public boolean strike;
    public int healthValue=100;//默认100，受击一次扣50，两次倒地
    public double getDistance(){return distance;}
    public boolean isStruck(){return strike;}
    public int getHealthValue(){
        return healthValue;
    }
    public void setDistance(double distance){
        this.distance=distance;
    }
    public void setStrike(boolean strike){
        this.strike=strike;
    }
    public void setHealthValue(int healthValue){
        this.healthValue=healthValue;
    }
    public Simulator(){
        double distance=0.0;
        boolean strike=false;
        int healthValue=100;
    }
    public void println(Object o){System.out.println(o);}
    public void print(Object o){System.out.print(o);}
    public void println(){System.out.println();}

    public synchronized long justifyIsStruck(){
        println("是否发生恐惧震慑？");
        Scanner scanner=new Scanner(System.in);
        strike=scanner.nextBoolean();
        scanner.nextLine();
        if(strike){println("发生恐惧震慑！");}
        else{println("没有发生恐惧震慑！");}
        return 1000;
    }
    public long justifyIsInteraction(){
        println("求生者交互！");
        return 1000;
    }
}

/****************************************************恐惧震慑子类****************************************************/
class SimulatorChild extends Simulator_DeepSeek {

    Scanner scanner =new Scanner(System.in);

    /**总游戏时间线程，设置游戏对局12秒**/
    public final long gameTotalTime=12000l;
    public long inputQForSurvivorOnCurrentTime;             //求生者交互时的瞬时时间
    public long inputEForHunterOnCurrentTime;               //监管者攻击时的瞬时时间

    Thread t1=new Thread();
    public static final double strikeRange=3.54;        //攻击范围3.54米
    public void println(Object o){System.out.println(o);}
    public void print(Object o){System.out.print(o);}
    public void println(){System.out.println();}

    /*************************游戏开始函数*****************************/
    public void runGame(){
        try{

            Thread startGameThread=new Thread();
            startGameThread.sleep(gameTotalTime);
            //设计时间计时器
            Thread calculatorOnTime = null;
            println();
            for(int i=0;i<=gameTotalTime/1000;i++){
                calculatorOnTime.sleep(1000);
                print("\r总游戏对局"+i+"秒");
            }
            /// /////////
        }catch (InterruptedException e) {
            println("游戏线程中断！");
        }finally {
            println("游戏中断结束！");
            return;
        }
    }
    /*************************游戏开始函数|*****************************/

    /*************************判定交互**********************************/
    public void inputQ(String input_s){
        if(!"Q".equals(input_s))throw new IllegalArgumentException("请输入Q!");
        else{println("交互成功！");}
    }
    /*********************************交互函数*****************************************/
    @Override
    public synchronized long justifyIsInteraction(){
        try{
            Runnable timeOfSurvivor=()->{
                Thread timeOfSurvivor_thread=new Thread();
                try {
                    timeOfSurvivor_thread.sleep(gameTotalTime);
                    println("按Q键触发交互！");
                    String new_input_s=scanner.next();
                    scanner.nextLine();
                    inputQ(new_input_s);
                    if(new_input_s.equals("Q")){
                        inputQForSurvivorOnCurrentTime =System.currentTimeMillis(); //记录摁下Q的瞬时时刻t1_survivor_q
                        println("交互时间："+ inputQForSurvivorOnCurrentTime/1000+"秒");
                    }else{
                       throw new IllegalArgumentException("请输入Q!");
                    }
                } catch (InterruptedException e) {
                    println("求生者时间线程中断！");
                }finally {
                    timeOfSurvivor_thread.currentThread().interrupt();
                }
            };
            Thread survivor=new Thread(timeOfSurvivor,"求生者线程");
            survivor.start();
        }catch(InputMismatchException e){
            println("请输入正确文本！");
        }
        return inputQForSurvivorOnCurrentTime;
    }

    /********************************判定攻击***************************************/
    public void inputE(String input_E){
        if("E".equals(input_E)){
            println("监管者攻击！");
        }else{
            throw new IllegalArgumentException("监管者攻击无效或未攻击！");
        }
    }
    @Override
    public synchronized long justifyIsStruck(){
        try{
            Runnable timeOfHunter = ()->{
                Thread timeOfHunter_thread = new Thread();
                try {
                    timeOfHunter_thread.sleep(gameTotalTime);  // 等待攻击时机
                    println("按E键触发监管者攻击！");
                    String new_input_e = scanner.next();
                    scanner.nextLine();
                    inputE(new_input_e);
                    if(new_input_e.equals("E")){
                        inputEForHunterOnCurrentTime=System.currentTimeMillis();        //监管者交互瞬时时间
                    }else throw new IllegalArgumentException("请输入E!");
                } catch (InterruptedException e) {
                    println("监管者线程中断！");
                } finally {
                    timeOfHunter_thread.currentThread().interrupt();
                }
            };
            Thread hunter = new Thread(timeOfHunter, "监管者线程");
            hunter.start();

        } catch(InputMismatchException e){
            println("请输入正确文本！");
        }
        return inputEForHunterOnCurrentTime;
    }
    public void test(){
        justifyIsInteraction();
        justifyIsStruck();
    }
}

public class FearShockSimulatorV{
    public static void main(String[] a){
       SimulatorDeepSeekChild sc=new SimulatorDeepSeekChild();
       sc.test();
    }
}