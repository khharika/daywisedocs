package Day24;

//Director (optional)
public class LaptopDirector {
    private LaptopBuilder laptopBuilder;

    public LaptopDirector(LaptopBuilder laptopBuilder) {
        this.laptopBuilder = laptopBuilder;
    }

    public Laptop constructLaptop() {
        return laptopBuilder
                .buildMemory(16) // Example: 16GB RAM
                .buildStorage(512) // Example: 512GB SSD
                .build();
    }
}