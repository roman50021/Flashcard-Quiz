package com.myapp.menu.io;

import java.util.Scanner;

public class ConsoleIO {
    private final Scanner in = new Scanner(System.in);

    public void println(String s){ System.out.println(s); }
    public void print(String s){ System.out.print(s); }

    public String readLine(String prompt) {
        System.out.print(prompt);
        return in.nextLine();
    }

    public String readNonEmpty(String prompt){
        while (true){
            String s = readLine(prompt);
            if (s != null && !s.isBlank()) return s.trim();
            println("Порожнє значення. Спробуй ще раз.");
        }
    }
}
