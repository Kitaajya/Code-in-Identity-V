import java.util.HashMap;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

abstract class People{
    public String name;             //姓名
    public int id;                  //学号/工号
    public int grade  ;             //年级
    /*增删改查*/
    abstract void add();
    abstract void delete();
    // abstract void search();
    abstract void edit();
    public People(String name, int id,int grade){
        this.name=name;this.id=id;this.grade=grade;
    }
}
class Student extends People{
    Scanner scanner=new Scanner(System.in);
    static int MAX_NUM=7;           //学校最大容量7人
    public Student(String name,int id,int grade){
        super(name,id,grade);
    }
    static void println(Object o){IO.println(o);}
    static void print(Object o){IO.print(o);}
    //设置Hashtable<name,id>
    Hashtable<String,Integer> student1=new Hashtable<>();
    //HashMap<String,Integer> student=new HashMap<>();
    @Override
    synchronized void add(){
        try{
            //一开始学校为空

            print("请输入你要添加的人数：");
            int n=scanner.nextInt();
            IO.println();
            if(n>7||n<0){
                throw new IllegalArgumentException("学校容纳不了这么多人！学生人数不能为负数！！");
            }else{
                //添加学生的核心逻辑
                for(int j=0;j<n;j++){
                    print("请输入姓名："+"\t");name=scanner.next();
                    print("请添加学号："+"\t");id=  scanner.nextInt();
                    scanner.nextLine();
                    student1.put(name,id+2026000);
                }
                println(student1);
            }
        }catch (InputMismatchException e) {
            System.out.println("请输入合法的数字！");
            scanner.nextLine(); // 清空错误输入
        }
    }
    //add()方法结束
    //delete()删除
    @Override
    synchronized void delete(){
        try{        print("请输入你要删除的学生学号：");
            String targetName=null;
            int willId=scanner.nextInt();scanner.nextLine();
            //查找：
            for(String name:student1.keySet()){
                if(student1.get(name)==willId){
                    targetName=name;
                    break;
                }
            }
            if(targetName!=null){
                student1.remove(targetName);
                //为了更逼真，增加延迟效果，lambda表达式重写run方法，ScheduleThreadPoolExecutor线程池操作延时
                Runnable runDelete=()->{println("成功删除！");};
                ScheduledThreadPoolExecutor deleteSchedule=new ScheduledThreadPoolExecutor(1);
                deleteSchedule.schedule(runDelete,3, TimeUnit.SECONDS);
            }else{println("未找到该学生。");}
        }catch (InputMismatchException ed){
            println("输入内容不合法！");}

    }
    @Override
    synchronized void edit(){
        print("请输入你要编辑的学生的学号：");
        IO.println();
        int willId=scanner.nextInt();
        scanner.nextLine();//清空换行
        for(String name:student1.keySet()){
            if(willId== student1.get(name)) {break;}
        }
        if(willId== student1.get(name)){
            print("这是你将修改的信息学号："+student1.get(name));
            IO.println();
            print("请重新给此学号的学生命名：");
            String newName=scanner.next();
            scanner.nextLine();
            name=newName;
            println("这是新的学生信息："+newName);
            student1.put(newName,willId);
            println("更新学生信息如下：");
            print(student1);
        }else{
            println("找不到此人信息！");
        }
    }
    //edit()函数结束

    public static void studentSelfMain(){
        Scanner scanner=new Scanner(System.in);
        Student s=new Student("测试添加人数",2,13);
        println("正在进入菜单，请稍等…………");
        try{
            Thread.sleep(3000);
            println("菜单正在加载中…………");
        }catch (InterruptedException eMain){
            println("线程被打断！");
        }
        Runnable enterMenu=()->{
            print("=======================菜单=============================");
            IO.println();
            print("请输入你的需求：");
            try{
                while(true){
                    String choice=scanner.next();scanner.nextLine();
                    switch (choice){
                        case"A"->s.add();
                        case"B"->s.edit();
                        case"C"->s.delete();
                        case"D"->{println("感谢使用！");return;}
                    }
                }

            }catch (IllegalArgumentException ec){print("不存在此选项！");}
            IO.println(s);
        };
        ScheduledThreadPoolExecutor MainEnterMenu=new ScheduledThreadPoolExecutor(1);
        MainEnterMenu.schedule(enterMenu,5,TimeUnit.SECONDS);

    }
}

class SchoolSystem{
    void main() {
    Student.studentSelfMain();
}}
