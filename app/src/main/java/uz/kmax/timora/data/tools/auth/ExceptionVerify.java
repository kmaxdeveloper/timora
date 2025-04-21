package uz.kmax.timora.data.tools.auth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExceptionVerify extends Throwable{
    public ExceptionVerify(String str) {
        super(str);
    }

    /**
     * - Emailni aniqlash uchun Regular Exception (REGEX)
     * - Bu kod Emaillikka tekshiradi .
     * */
    public static void emailCheck(String email) throws ExceptionVerify{
        String email_Regex = "^([a-z0-9._%+-]{3,20})+@([a-z0-9.-]{4,20})+\\.([a-z]){2,6}+$";
        Pattern pattern = Pattern.compile(email_Regex);
        Matcher matcher = pattern.matcher(email);
        boolean checkEmail = false;
        while (matcher.find()){
            checkEmail = true;
        }
        if (checkEmail){
            System.out.println("COOL Next Step ->");
        }else {
            throw new ExceptionVerify("Email is Wrong Please reWrite Email !!! ");
        }
    }

    /**
     * - Password Check code
     * - Bu kod Parollikka tekshiradi :
     * - Bitta Katta harf
     * - Bitta Kichik harf
     * - Bitta belgi
     * - Uzunligi min : 8 , max : 20
     * */
    static void passwordCheck(String password) throws ExceptionVerify {
        String password_Regex = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=.*[@#$%^&\\-+=()])(?=\\S+$).{8,20}$";
        Pattern pattern = Pattern.compile(password_Regex);
        Matcher matcher = pattern.matcher(password);
        boolean checkPassword = false;
        while (matcher.find()){
            checkPassword = true;
        }
        if (checkPassword){
            System.out.println("Well Done Next Step -> ");
        }else {
            throw new ExceptionVerify("Password Wrong !!! ");
        }
    }

    /**
     * - Bu kod Ismlikka tekshiradi
     * - Agar foydalanuvchi behosdan String o'rniga raqam yoki belgi kiritib qo'ysa Xatolik paydo bo'ladi .
     * - Uzunligi Min : 3 , Max : 10.
     * - */
    static void checkName(String name) throws ExceptionVerify {
        String name_Regex = "^([a-z|A-Z]){3,10}+$";
        Pattern pattern = Pattern.compile(name_Regex);
        Matcher matcher = pattern.matcher(name);
        boolean checkName = false;
        while (matcher.find()){
            checkName = true;
        }
        if (checkName){
            System.out.println("Good Job "+name+" Next Step -> ");
        }else {
            throw new ExceptionVerify("Name Is Wrong !!! ");
        }
    }

    /**
     * - Yoshni aniqlash uchun Regular Exception
     * - 0 dan to 999 gacha kiritish mumkin
     * */
    static void ageCheck(String age) throws ExceptionVerify {
        String age_Regex = "^([0-9]){1,3}+$";
        Pattern pattern = Pattern.compile(age_Regex);
        Matcher matcher = pattern.matcher(age);
        boolean checkAge = false;
        while (matcher.find()){
            checkAge = true;
        }
        if (checkAge){
            System.out.println("COOL !!!");
        }else {
            throw new ExceptionVerify("Age is Wrong !!!");
        }
    }
}
