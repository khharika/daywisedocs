package Day20;
public class SRP_Imple {
    public static void main(String[] args) {
        Customer cobj = new Customer("Rasunamba", "C001");
        ManagingFiles mobj = new ManagingFiles();
        mobj.saveData(cobj);
    }
}
