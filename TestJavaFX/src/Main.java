import javax.swing.*;
import java.awt.*;

class GameFrame {
    public final int WIDTH=800;
    public final int HEIGH =800;
    public JFrame jf=new JFrame("横向跑酷");
    int x=WIDTH;
    int y= HEIGH;
    public void area(){

        jf.setSize(WIDTH,HEIGH);
        jf.getContentPane().setBackground(Color.BLACK);     //设置游戏时间：夜晚，背景纯黑
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setLayout(null);
        //设置地面
        JPanel ground =new JPanel();
        ground.setBackground(Color.GRAY);
        ground.setBounds(0,600,WIDTH,WIDTH/4);
        jf.add(ground);
        // JPanel sand=new JPanel();       //向地面中添加沙子
        // sand.setSize(10,12);
        // jf.add(sand);


        //==================
        jf.setVisible(true);

    }

}




public class Main{
    public static void main(String[] args){
        GameFrame jf=new GameFrame();
        jf.area();
    }
}