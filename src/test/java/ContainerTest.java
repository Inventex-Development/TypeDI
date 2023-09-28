import org.inventex.typedi.Container;
import org.inventex.typedi.Inject;
import org.inventex.typedi.Service;

public class ContainerTest {
    public static void main(String[] args) {
        TestService service = Container.get(TestService.class);
        service.test();

        OtherService other = Container.get(OtherService.class);

        System.out.println(other == service.other);
    }

    @Service
    static class TestService {
        @Inject
        public OtherService other;

        public void test() {
            System.out.println("test method");
            other.other();
        }
    }

    @Service
    static class OtherService {
        public void other() {
            System.out.println("other method");
        }
    }
}
