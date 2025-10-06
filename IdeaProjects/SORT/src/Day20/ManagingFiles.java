package Day20;
import java.io.FileWriter;
import java.io.IOException;

public class ManagingFiles {
    public void saveData(Customer customer) {
        try {
            FileWriter fw = new FileWriter(customer.getName() + ".txt");
            fw.write("The customer name is " + customer.getName() + "\t");
            fw.write("The customer id is " + customer.getCustID() + "\t");
            fw.close();
            System.out.println("The data is saved in the file with your name.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
