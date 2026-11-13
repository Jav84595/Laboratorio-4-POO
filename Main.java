import controlador.AcopioGTController;
import modelo.AcopioGTModel;
import vista.AcopioGTView;

public class Main {

    public static void main(String[] args) {
        AcopioGTModel model = new AcopioGTModel();
        AcopioGTView view = new AcopioGTView();
        AcopioGTController controller = new AcopioGTController(model, view);
        controller.iniciar();
    }
}
