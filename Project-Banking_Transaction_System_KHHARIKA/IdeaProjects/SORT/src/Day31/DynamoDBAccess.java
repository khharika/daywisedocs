package Day31;
import lombok.Data;

import java.util.List;
@Data
@DynamoDBTable(tableName="Students")
class Students {
    @DynamoDBHashKay(attributeName="ID")
    private String ID;
    @DynamoDBAttribute(attributeName="SName")
    private String Name;
}
//DynamoDB Access
class DynamoDBAccess {
    private DynamoDBMapper Mapper = new DynamoDBMapper((AmazonDynamoDB) AmazonDynamoDBClientBuilder.standard().build());
    public Students getItem(Students item) {
        return Mapper.load(Students.class, item.getID());
    }
    public void saveItem(Students item) {
        Mapper.save(Item);
    }
    public List<Students> getItems(Students item) {
        return Mapper.query(Students.class, new DynamoDBQueryExpression().withHashKeyValues(item));
    }
}
public class DynamoDBMapperClass {
    public static void main(String[] args) {
    }
}