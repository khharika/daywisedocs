package Day25;
public class BillingCounterService {
    public void payBill(String accountId, String billId, double amount) {
        System.out.println(
                "Paying " + amount +
                        " for billId " + billId +
                        " from account " + accountId
        );
    }
}
