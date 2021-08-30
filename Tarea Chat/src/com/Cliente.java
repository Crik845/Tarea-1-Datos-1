package com;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.*;

public class Cliente {
    JFrame ventana_chat = null;
    JButton boton_enviar = null;
    JTextField txt_mensaje1 = null;
    JTextField txt_mensaje2 = null;
    JTextField txt_mensaje3 = null;
    JTextArea area_chat = null;
    JLabel precio = null;
    JLabel impuesto = null;
    JLabel peso = null;
    JPanel contenedor_areachat = null;
    JPanel contnedor_botontexto = null;
    JPanel contenedor_labels = null;
    JScrollPane scroll = null;
    ServerSocket servidor = null;
    Socket socket = null;
    BufferedReader lector = null;
    PrintWriter escritor = null;

    public Cliente() {
        hacerInterfaz();

    }
    public void hacerInterfaz() {
        ventana_chat = new JFrame("Cliente");
        boton_enviar = new JButton("Enviar");
        txt_mensaje1 = new JTextField(4);
        txt_mensaje2 = new JTextField(5);
        txt_mensaje3 = new JTextField(6);
        area_chat = new JTextArea(20, 12);
        precio = new JLabel("Precio ");
        impuesto = new JLabel("Impuesto");
        peso = new JLabel("Peso");
        scroll = new JScrollPane(area_chat);
        contenedor_areachat = new JPanel();
        contenedor_areachat.setLayout(new GridLayout(1, 1));
        contenedor_areachat.add(scroll);
        contenedor_labels = new JPanel();
        contenedor_labels.setLayout(new GridLayout(1, 1));
        contenedor_labels.add(precio);
        contenedor_labels.add(impuesto);
        contenedor_labels.add(peso);
        contnedor_botontexto = new JPanel();
        contnedor_botontexto.setLayout(new GridLayout(1, 4));
        contnedor_botontexto.add(txt_mensaje1);
        contnedor_botontexto.add(txt_mensaje2);
        contnedor_botontexto.add(txt_mensaje3);
        contnedor_botontexto.add(boton_enviar);
        ventana_chat.setLayout(new BorderLayout());
        ventana_chat.add(contenedor_labels, BorderLayout.CENTER);
        ventana_chat.add(contenedor_areachat, BorderLayout.NORTH);
        ventana_chat.add(contnedor_botontexto, BorderLayout.SOUTH);
        ventana_chat.setSize(400, 400);
        ventana_chat.setVisible(true);
        ventana_chat.setResizable(false);
        ventana_chat.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Thread principal = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket("Localhost", 9000);
                    leer();
                    escribir();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        principal.start();
    }
    public void leer() {
        Thread leer_hilo = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    while (true) {
                        String mensaje_recibido = lector.readLine();
                        area_chat.append("Servidor dice: " + mensaje_recibido + "\n");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        leer_hilo.start();
    }
    public void escribir() {
        Thread escribir_hilo = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    escritor = new PrintWriter(socket.getOutputStream(), true);
                    boton_enviar.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String enviar_mensaje1 = txt_mensaje1.getText();
                            String enviar_mensaje2 = txt_mensaje2.getText();
                            String enviar_mensaje3 = txt_mensaje3.getText();
                            int precio = Integer.parseInt(enviar_mensaje1);
                            int impuesto = Integer.parseInt(enviar_mensaje2);
                            int peso = Integer.parseInt(enviar_mensaje3);
                            escritor.println(precio);
                            escritor.println(impuesto);
                            escritor.println(peso);
                            escritor.println(calculo(precio,impuesto,peso));
                            txt_mensaje1.setText("");
                            txt_mensaje2.setText("");
                            txt_mensaje3.setText("");
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        escribir_hilo.start();
    }
    public double calculo(int precio, int impuesto, int peso) {
        double total = (precio * impuesto / 100) + (peso * 0.15d);
        return total;
    }
    public static void main (String[]args){
        new Cliente();
    }
}