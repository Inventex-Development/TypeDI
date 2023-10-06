import dev.inventex.typedi.ConstructWith;
import dev.inventex.typedi.Container;
import dev.inventex.typedi.Service;

public class MultipleConstructorsTest {
    public static void main(String[] args) {
        MyService service = Container.get(MyService.class);
        System.out.println(service.value);
    }

    @Service
    public static class MyService {
        public int value;

        public MyService(int value) {
            this.value = value;
        }

        @ConstructWith
        public MyService() {
            this.value = 100;
        }
    }
}
