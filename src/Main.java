import java.io.IOException;

public class Main {
    public static void main(String[] args) throws Exception {
        MyFrane frame = new MyFrane();
        frame.programHandler.setParentFrame(frame);
    }
}