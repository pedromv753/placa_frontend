package pe.edu.cibertec.buscador_placa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import pe.edu.cibertec.buscador_placa.viewmodel.BasicModel;
import pe.edu.cibertec.buscador_placa.viewmodel.VehicleRequest;
import pe.edu.cibertec.buscador_placa.viewmodel.VehicleResponse;

@Controller
@RequestMapping("/vehiculo")
public class PlacaController {

    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/inicio")
    public String inicio(Model model) {
        BasicModel basicModel = new BasicModel("00","","");
        model.addAttribute("basicModel",basicModel);
        return "index";
    }

    @PostMapping("/autenticacion")
    public String autenticar(@RequestParam("placa") String placa, Model model) {

        // Expresión regular para validar placa alfanumérica de 8 caracteres
        String regex = "^[a-zA-Z]{3}-[0-9]{3}$";

        if (placa == null || placa.trim().isEmpty()) {
            BasicModel basicModel = new BasicModel("01", "Error: No puede dejar vacio la placa", "");
            model.addAttribute("basicModel", basicModel);
            return "index";
        }

        // Validar que la placa cumpla con la longitud y formato
        if (!placa.matches(regex)) {
            BasicModel basicModel = new BasicModel("02", "Error: Debe ingresar una placa correcta", "");
            model.addAttribute("basicModel", basicModel);
            return "index";
        }

        String apiUrl = "http://localhost:8081/authentication/consulta";
        VehicleRequest vehicleRequest = new VehicleRequest(placa);

        VehicleResponse response = restTemplate.postForObject(apiUrl, vehicleRequest, VehicleResponse.class);

        if (response != null && "1".equals(response.codigo()) || "2".equals(response.codigo())) {
            // Pasa la información del vehículo al modelo para usar en el template placas.html
            model.addAttribute("placa", response.placa());
            model.addAttribute("marca", response.marca());
            model.addAttribute("modelo", response.modelo());
            model.addAttribute("nroAsientos", response.nroAsientos());
            model.addAttribute("precio", response.precio());
            model.addAttribute("color", response.color());

            return "placas";
        } else {
            BasicModel basicModel = new BasicModel("01", "No se encontró un vehículo para la placa ingresada", "");
            model.addAttribute("basicModel", basicModel);
            return "index";
        }
    }
}
