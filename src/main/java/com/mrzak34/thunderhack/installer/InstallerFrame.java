package com.mrzak34.thunderhack.installer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;


import java.net.*;

public class InstallerFrame extends JFrame{
    private static JTextField input = new JTextField("", 5);
    private JLabel label = new JLabel("                                 ThunderHack Installer");
    private JButton button1 = new JButton("Открыть проводник");
    private JButton button2 = new JButton("Установить");
    private JFileChooser filebrowser = new JFileChooser();


    public InstallerFrame(){
        super("ThunderHack Installer");
        input.setText(System.getProperty("user.home") + File.separator + "AppData"+ File.separator +"Roaming"+ File.separator +".minecraft"+ File.separator +"mods"+  File.separator);
        this.setBounds(100,100,350,300);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        filebrowser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        Container container = this.getContentPane();
        container.setLayout(new GridLayout(4,1));
        container.add(label);
        container.add(input);

        button2.addActionListener(new ButtonEventListener ());
        button1.addActionListener(new ButtonEventListener2 ());

        container.add(button1);
        container.add(button2);

    }


    class ButtonEventListener implements ActionListener {
        public void actionPerformed (ActionEvent e){
            downloadFile();
            if(noerror) {
                JOptionPane.showMessageDialog(null, "ThunderHack установлен", "Успех", JOptionPane.PLAIN_MESSAGE);
            }
        }
    }


    class ButtonEventListener2 implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (filebrowser.showOpenDialog(InstallerFrame.this) == JFileChooser.APPROVE_OPTION) {
                input.setText(filebrowser.getSelectedFile().getAbsolutePath());
            }
        }
    }


    public static void downloadFile(){
        try {
            URL url = new URL(getVersion());
            URLConnection openConnection = url.openConnection();
            boolean check = true;

            try {

                openConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                openConnection.connect();

                if (openConnection.getContentLength() > 8000000) {
                    check = false;
                }
            } catch (Exception e) {
                check = false;
                internetError();
                e.printStackTrace();
            }
            if (check) {
                try {

                    InputStream in = new BufferedInputStream(openConnection.getInputStream());
                    File file = new File( input.getText() + "/ThunderHack.jar");
                    BufferedOutputStream bufferedoutputstream = new BufferedOutputStream(new FileOutputStream(file));
                    byte[] buf = new byte[1024];
                    int n = 0;
                    while (-1 != (n = in.read(buf))) {
                        bufferedoutputstream.write(buf, 0, n);
                    }
                    bufferedoutputstream.close();
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e){
            internetError();
        }
    }

    public static String getVersion() {
        String versionNumber = null;
        try {
            final URL url = new URL("https://pastebin.com/raw/2iw0d4jQ");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            versionNumber = bufferedReader.readLine();
        }catch (Exception exception){
            fileError();
        }
        return versionNumber;
    }

    static boolean noerror = true;

    public static void internetError(){
        JOptionPane.showMessageDialog(null,"Проверь интернет!","Ошибка",JOptionPane.PLAIN_MESSAGE);
        noerror = false;
    }

    public static void fileError(){
        JOptionPane.showMessageDialog(null,"Ошибка записи файла! ","Ошибка",JOptionPane.PLAIN_MESSAGE);
        noerror = false;
    }
}
