package com;

import javax.swing.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.awt.event.*;

public class Servidor {

    // Declaración de variables

    JFrame ventana_chat = null; // Ventana principal
    JButton btn_enviar = null; // Boton para envier los datos

    // Campos de texto para ingresar los datos

    JTextField txt_mensaje1= null;
    JTextField txt_mensaje2= null;
    JTextField txt_mensaje3= null;

    JTextArea area_chat= null; // Area de texto para mostrar los dato

    // Contenedores
    JPanel contenedorChat= null;
    JPanel contenerdorBtntxt= null;

    JScrollPane scroll= null; // scroll

    // Sockets
    ServerSocket servidor = null;
    Socket socket= null;

    // Lector y escritor
    BufferedReader lector = null;
    PrintWriter escritor= null;

    // Labels
    JLabel precio= null;
    JLabel impuesto = null;
    JLabel peso = null;
    JPanel contenedor_labels = null;


    public Servidor(){
        hacerInterfaz();
    }

    public void hacerInterfaz(){

        // Toda la creación de los elementos que contiene la interfaz

        ventana_chat = new JFrame("Servidor"); // ventana para el servidor
        btn_enviar = new JButton("Enviar");// botón para enviar los datos

        // campos de texto para los datos
        txt_mensaje1 = new JTextField(4);
        txt_mensaje2 = new JTextField(5);
        txt_mensaje3 = new JTextField(6);
        area_chat= new JTextArea(20,12);

        scroll = new JScrollPane(area_chat); // scroll

        // labels
        precio = new JLabel("Precio ");
        impuesto = new JLabel("Impuesto");
        peso = new JLabel("Peso");

        // contenedores y tamaños
        contenedor_labels = new JPanel();
        contenedor_labels.setLayout(new GridLayout(1,1));
        contenedor_labels.add(precio);
        contenedor_labels.add(impuesto);
        contenedor_labels.add(peso);
        contenedorChat = new JPanel();
        contenedorChat.setLayout(new GridLayout(1,1));
        contenedorChat.add(area_chat);
        contenerdorBtntxt = new JPanel();
        contenerdorBtntxt.setLayout(new GridLayout(1,4));
        contenerdorBtntxt.add(txt_mensaje1);
        contenerdorBtntxt.add(txt_mensaje2);
        contenerdorBtntxt.add(txt_mensaje3);
        contenerdorBtntxt.add(btn_enviar);

        // características de la ventana principal y ubicciones de los contenedores
        ventana_chat.setLayout(new BorderLayout());
        ventana_chat.add(contenedor_labels, BorderLayout.CENTER);
        ventana_chat.add(contenedorChat, BorderLayout.NORTH);
        ventana_chat.add(contenerdorBtntxt, BorderLayout.SOUTH);
        ventana_chat.setSize(400,400);
        ventana_chat.setVisible(true);
        ventana_chat.setResizable(false);
        ventana_chat.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // hilo principal para el socket
        Thread principal = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                servidor = new ServerSocket(9000);

                    while (true){
                        socket = servidor.accept();
                        leer();
                        escribir();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        principal.start(); // se inicia el hilo

    }

    public void leer(){ // método para leer los datos
        Thread leer_hilo = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lector= new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        while (true){
                            String mensaje_recibido = lector.readLine();
                            area_chat.append("Cliente dice: " + mensaje_recibido +"\n");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        leer_hilo.start(); // se inicia el método para leer los datos
    }
    public void escribir() { // método para escribir los datos
        Thread escribir_hilo = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                escritor = new PrintWriter(socket.getOutputStream(), true);
                btn_enviar.addActionListener(new ActionListener() {
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
        escribir_hilo.start(); // se inicia el método para escribir los datos
    }
        public double calculo(int precio, int impuesto, int peso) {
            double total = (precio * impuesto / 100) + (peso * 0.15d);
            return total;
        }
    public static void main (String[]args){
        new Servidor();
    }
}