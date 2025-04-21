package uz.kmax.timora.data.tools.auth;

import java.util.Scanner;

public class Registration {
    public static void main(String[] args){
        System.out.println("Salom ! Foydalanuvchi !");
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Emailni kiriting : ");
            String email = scanner.next();
            ExceptionVerify.emailCheck(email);
            System.out.println();

            System.out.println("Parolni kiriting : ");
            String password = scanner.next();
            ExceptionVerify.passwordCheck(password);
            System.out.println();

            System.out.println("Ismingizni kiriting : ");
            String name = scanner.next();
            ExceptionVerify.checkName(name);
            System.out.println();

            System.out.println("Yoshingizni kiriting : ");
            String age = scanner.next();
            ExceptionVerify.ageCheck(age);

            System.out.println("Tabriklaymiz Siz Ro'yhatdan o'tdingiz !!! ");
        }catch (ExceptionVerify e){
            System.out.println(e);
        }
    }
}
