package Day20;
public class Main {
    public static void main(String[] args) {
        BankNotifications notify;

        notify = new EmailNotify();
        notify.sendOTP();

        notify = new MobileNotify();
        notify.sendOTP();

        notify = new WhatsappNotify();
        notify.sendOTP();
    }
}
