import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.awt.event.*; 

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

/*
    Working:
    Equal button of the calculator is equivalent to the stop button. When equal button is pressed, it is expected that
    the textbox has input as <val><operation><val> like 2+3; 8-87 and so on. For other inputs, the textbox is cleared. Space key to
    select the highlighted function and enter to select the highlighted number

    Implementation:
    4 threads:
    1. The main thread which displays the calculator
    2. Two Highlighter threads for function area and number area each
    3. A thread for Listening Key events and responding to it: either print something in display or compute the value and then display
*/
class Highlighter implements Runnable{
    // simple job of periodically highlighting this button array passed to it
    ArrayList <JButton> buttons;
    Boolean halt;
    // state keeps track of which button index is getting highlighted. The key Listener requests this state via
    // the Calculator class to know what has been entered
    Integer state;
    Integer size;
    public Highlighter(ArrayList <JButton> l){
        buttons = l;
        size = buttons.size();
        halt = false;
        state = 0;
    }
    public void run(){
        while(!halt){
           // System.out.println("here:"+state);
            buttons.get(state).setBackground(Color.WHITE);
            try {
                Thread.sleep(3000);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
            buttons.get(state).setBackground(Color.BLACK);
            state = (state +1)%size;
        }
    }
    public void stop(){
        halt = true;
    }
}


class CalculatorKeyListener implements KeyListener,Runnable{

    Boolean enter;
    Boolean space;
    public CalculatorKeyListener(){
        enter= false;
        space = false;
    }
    public void run(){
        while(true){
            //System.out.println("run");
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
           
            if(enter){
                // gets the number pointer and prints it on the screen
                String n = Calculator.getNumberPtr().toString();
                String text = Calculator.getText();
                Calculator.textbox.setText(text +  n);
               // System.out.println("enter");
                enter = false;
            }

            if(space){
                // gets the function pointer and prints it on the screen/takes appropriate action
                Integer func = Calculator.getFunctionPtr();
                String f;
                if(func == 0){
                    f = "+";
                }
                else if(func==1){
                    f ="-";
                }
                else if(func == 2){
                    f="*";
                }
                else if(func==3){
                    f = "/";
                }
                else if(func ==4){
                    f=Calculator.compute();
                    Calculator.textbox.setText("");
                }
                else{
                    f="";
                    Calculator.textbox.setText("");
                }
                String text = Calculator.getText();
                if(Calculator.getText().equals("")){
                    Calculator.textbox.setText(f);
                }
                else{
                    Calculator.textbox.setText(text + f);
                }
                
             //   System.out.println("space");
                space = false;
            }
        }
    }
    public void keyPressed(KeyEvent e){
     //   System.out.println("pressed:"+e.getKeyCode());
        if(e.getKeyCode()==KeyEvent.VK_ENTER){
            enter = true;
        }
        if(e.getKeyCode()==KeyEvent.VK_SPACE){
            space = true;
        }


    }
    public void keyTyped(KeyEvent e){
        
    }
    public void keyReleased(KeyEvent e){
        
    }
}

class Calculator extends JFrame{
    static JFrame frame;
    static JTextField textbox;
    static ArrayList<JButton> function;
    static ArrayList<JButton> numbers;
    static JPanel panel;
    private static Highlighter function_highlighter;
    private static Highlighter number_highlighter;
    static private CalculatorKeyListener KL;
    public static Integer getFunctionPtr() {
        return function_highlighter.state;
    }

    public static String getText() {
        return textbox.getText();
    }

    public static Integer getNumberPtr() {
        return number_highlighter.state;
    }
    public static String compute(){
        String t = textbox.getText();
        Integer a,b;
        Integer i = 0;
        String s="";
        while(i<t.length() && '0'<= t.charAt(i) && t.charAt(i)<='9'){
            s+=t.charAt(i);
            i++;
        }
        if(s==""){
            return "";
        }
        a = Integer.parseInt(s);
        s="";
        char op = t.charAt(i);
        i++;
        while(i<t.length() && '0'<= t.charAt(i) && t.charAt(i)<='9'){
            s+=t.charAt(i);
            i++;
        }
        b = Integer.parseInt(s);
        if(s==""){
            return "";
        }
        if(op=='+'){
            a = a+b;
            return a.toString();
        }
        else if(op=='-'){
            a = a-b;
            return a.toString();
        }
        else if(op=='*'){
            a = a*b;
            return a.toString();
        }
        else if(op=='/'){
            if(!b.equals(0)){
                a = a/b;
                return a.toString();
            }
            else{
                return "";
            }
            
        }
        else{
            return "";
        }
    }
    public static void main(String[] args) {
        // intializing all the components of UI
        KL = new CalculatorKeyListener();
        frame = new JFrame("Calculator");
        function = new ArrayList<JButton>();
        numbers = new ArrayList<JButton>();
        panel = new JPanel();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        textbox = new JTextField();
        textbox.setOpaque(true);
        textbox.setEditable(false);
        JButton B;
        B = new JButton("+");
        function.add(B);
        B = new JButton("-");
        function.add(B);
        B = new JButton("*");
        function.add(B);
        B = new JButton("/");
        function.add(B);
        B = new JButton("=");
        function.add(B);
        B = new JButton("CLEAR");
        function.add(B);
        B = new JButton("0");
        numbers.add(B);
        B = new JButton("1");
        numbers.add(B);
        B = new JButton("2");
        numbers.add(B);
        B = new JButton("3");
        numbers.add(B);
        B = new JButton("4");
        numbers.add(B);
        B = new JButton("5");
        numbers.add(B);
        B = new JButton("6");
        numbers.add(B);
        B = new JButton("7");
        numbers.add(B);
        B = new JButton("8");
        numbers.add(B);
        B = new JButton("9");
        numbers.add(B);
        Integer i;
        JLabel lbl = new JLabel("Display Area");
        lbl.setPreferredSize(new Dimension(800, 100));
        textbox.setPreferredSize(new Dimension(800, 100));
        panel.add(lbl);
        panel.add(textbox);
        lbl = new JLabel("Number Keys");
        lbl.setPreferredSize(new Dimension(800, 100));
        panel.add(lbl);
        for (i = 0; i < numbers.size(); i++) {
            numbers.get(i).setPreferredSize(new Dimension(400, 40));
            numbers.get(i).setOpaque(true);
            panel.add(numbers.get(i));
        }
        lbl = new JLabel("Function Keys");
        lbl.setPreferredSize(new Dimension(800, 100));
        panel.add(lbl);
        for (i = 0; i < function.size(); i++) {
            function.get(i).setPreferredSize(new Dimension(500, 40));
            function.get(i).setOpaque(true);
            panel.add(function.get(i));
        }

        panel.setBackground(Color.GRAY);
        panel.setFocusable(true);
        // set keylistener. 
        panel.addKeyListener(KL);
        frame.add(panel);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setSize(1000, 1000);
        frame.setVisible(true);
        // threads for each function highlighter and number highlighter
        function_highlighter = new Highlighter(function);
        number_highlighter = new Highlighter(numbers);
        Thread t1,t2,t3;
        t1 = new Thread(function_highlighter);
        t2 = new Thread(number_highlighter);
        // a thread for keylistener
        t3 = new Thread(KL);
        t1.start();
        t2.start();
        t3.start();
    }
}