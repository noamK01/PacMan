package PacMan;


import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Pac-Man");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(new PacMan());

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        System.out.println(frame.getHeight());
        System.out.println(frame.getWidth());


    }

}

