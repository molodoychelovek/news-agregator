package sample.css;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class test {
    public static void main(String[] args) throws IOException {
        JFrame fr = new JFrame();
        JPanel panel = new JPanel();
        panel.add(new JButton(){{
            setBounds(25, 25, 25, 25);
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        System.out.println("+");
                    }
                }
            });
        }});

        fr.add(panel);
        fr.setSize(100, 100);
        fr.setVisible(true);
    }
}
