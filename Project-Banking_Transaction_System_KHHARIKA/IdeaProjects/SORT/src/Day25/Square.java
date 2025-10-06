package Day25;
public class Square extends Shape {
    private int s;
    public Square(int s, ExcalidrawAPI excalidrawAPI) {
        super(excalidrawAPI);
        this.s = s;
    }
    @Override
    public void draw() {
        excalidrawAPI.drawSquare(s);
    }
}
