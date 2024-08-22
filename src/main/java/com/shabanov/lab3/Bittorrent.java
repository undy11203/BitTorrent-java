package com.shabanov.lab3;

import com.shabanov.lab3.controller.MainController;

import com.shabanov.lab3.utils.Console;

/**
 * Hello world!
 *
 */
public class Bittorrent {

    public static void main( String[] args ) {
        try {
            Console console = new Console("BitTorrent", "picture/icon.jpg");
            console.show();

            MainController controller;
            if(args.length > 0){
                controller = new MainController(args);
            }else{
                controller = new MainController();
            }

        }catch (Exception e) {
            System.out.println("error");
            e.printStackTrace();
        }
    }
}
