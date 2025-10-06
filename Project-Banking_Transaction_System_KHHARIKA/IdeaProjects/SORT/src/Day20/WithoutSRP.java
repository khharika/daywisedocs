package Day20;
import java.io.FileWriter;
import java.io.IOException;

public class WithoutSRP {
    String name;
    String custID;

    public WithoutSRP(String name, String custID) {
        this.name = name;
        this.custID = custID;
    }

    public String getName() {
        return name;
    }

    public String getCustID() {
        return custID;
    }

    public void saveData() {
        try {
            FileWriter fw = new FileWriter(name + ".txt");
            fw.write("The customer name is " + name + "\t");
            fw.write("The customer id is " + custID + "\t");
            fw.close();
            System.out.println("The data is saved in the file with your name.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        WithoutSRP cobj = new WithoutSRP("Rasunamba", "C001");
        cobj.saveData();
    }
}
