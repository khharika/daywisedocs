package Day24;
public class BlackConcreteprototype implements Colors {
    private String name;


    public BlackConcreteprototype() {
        System.out.println(" BlackConcreteprototype constructor is called");
    }

    public BlackConcreteprototype(String name) {
        this.name = name;
    }

    public Colors clone() {
        return new BlackConcreteprototype(this.name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

