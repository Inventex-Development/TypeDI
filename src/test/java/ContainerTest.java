import dev.inventex.typedi.Container;
import dev.inventex.typedi.Factory;
import dev.inventex.typedi.Inject;
import dev.inventex.typedi.Service;

public class ContainerTest {
    public static void main(String[] args) {
        TestService service = Container.get(TestService.class);
        service.test();

        OtherService other = Container.get(OtherService.class);

        System.out.println(other == service.other);
    }

    @Service(global = true, factory = TestFactory.class)
    static class TestService {
        @Inject
        public OtherService other;

        public void test() {
            System.out.println("test method");
            other.other();
        }
    }

    static class TestFactory implements Factory<TestService> {
        @Override
        public TestService create() {
            TestService service = new TestService();
            service.other = new OtherService();
            return service;
        }
    }

    @Service
    static class OtherService {
        public void other() {
            System.out.println("other method");
        }
    }
}
