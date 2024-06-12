package core;

import javax.swing.*;
import java.awt.*;

public class Helper {

    public static void setTheme() {
        for (UIManager.LookAndFeelInfo info:UIManager.getInstalledLookAndFeels()) {
            if("Nimbus".equals(info.getName())) {
                try {
                    UIManager.setLookAndFeel(info.getClassName());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                break;
            }
        }
    }

    public static void setLocation(JFrame jFrame) {
        int x = (Toolkit.getDefaultToolkit().getScreenSize().width - jFrame.getSize().width) / 2;
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height - jFrame.getSize().height) / 2;
        jFrame.setLocation(x,y);
    }

    public static boolean isFieldEmpty(JTextField field) {
        return field.getText().trim().isEmpty();
    }

    public static boolean isFieldListEmpty(JTextField[] fields) {
        for (JTextField field:fields) {
            if (isFieldEmpty(field)) return true;
        }
        return false;
    }

    public static void showMessage(String string) {
        optionPaneTR();
        String message;

        String title = switch (string) {
            case "fill" -> {
                message = "Tüm alanları doldurunuz!";
                yield "Hata!";
            }
            case "done" -> {
                message = "İşlem başarılı";
                yield "Sonuç";
            }
            case "notFound" -> {
                message = "Kayıt bulunamadı";
                yield "Bulunamadı";
            }
            case "error" -> {
                message = "Hatalı işlem yaptınız";
                yield "Hata";
            }
            default -> {
                message = string;
                yield "Mesaj";
            }
        };
        JOptionPane.showMessageDialog(null,message,title,JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirm (String str) {
        optionPaneTR();
        String msg;
        if (str.equals("sure")) {
            msg = "Bu işlemi yapmak istediğine emin misin?";
        } else {
            msg = str;
        }
        return JOptionPane.showConfirmDialog(null,msg,"Emin misin?",JOptionPane.YES_NO_OPTION)==0;
    }

    public static void optionPaneTR() {
        UIManager.put("OptionPane.okButtonText","Tamam");
        UIManager.put("OptionPane.yesButtonText","Evet");
        UIManager.put("OptionPane.noButtonText","Hayır");
    }

}
