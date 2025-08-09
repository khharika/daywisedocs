package Day25;
public class CompositeMethodDP {
    public static void main(String[] args) {
        System.out.println("Composite Method DP - Structural DP");
        // Leaf
        Company softwareDept = new Software(1, "Application Development");
        // Composite
        CompanyHead companyHead = new CompanyHead(3, "ABC Company");
        companyHead.addDepartment(softwareDept);
        // Display structure
        companyHead.displayName();
    }
}
