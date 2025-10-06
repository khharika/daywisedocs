package Day25;
import java.util.ArrayList;
import java.util.List;
// Composite component
public class CompanyHead implements Company {
    private int id;
    private String name;
    private List<Company> subDepartments;
    public CompanyHead(int id, String name) {
        this.id = id;
        this.name = name;
        this.subDepartments = new ArrayList<>();
    }
    @Override
    public void displayName() {
        System.out.println("Company Head: " + name + " (ID: " + id + ")");
        subDepartments.forEach(Company::displayName);
    }
    public void addDepartment(Company company) {
        subDepartments.add(company);
    }
    public void removeDepartment(Company company) {
        subDepartments.remove(company);
    }
}

