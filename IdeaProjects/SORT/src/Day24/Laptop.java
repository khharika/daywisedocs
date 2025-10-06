package Day24;
// product Component
class Laptop {
    private int memory;
    private int storage;

    public Laptop() {}

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public int getStorage() {
        return storage;
    }

    public void setStorage(int storage) {
        this.storage = storage;
    }

    @Override
    public String toString() {
        return "Laptop [Memory=" + memory + "GB, Storage=" + storage + "GB]";
    }
}