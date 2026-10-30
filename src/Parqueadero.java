import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

public class Parqueadero {
    static final int CAPACIDAD_TOTAL = 10;
    static final int TARIFA_HORA_CARRO = 4000;
    static final int TARIFA_DIA_CARRO = 30000;
    static final int TARIFA_HORA_MOTO = 2000;
    static final int TARIFA_DIA_MOTO = 15000;
    static final int UMBRAL_HORAS_DIA = 8;
    static final int HORA_APERTURA = 8;
    static final int HORA_CIERRE = 22;

    static final int OPCION_SALIR = 5;

    static final String CARRO = "Carro";
    static final String MOTO = "Moto";

    static final Scanner SCANNER = new Scanner(System.in);
    static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    static String[] tipoVehiculo = new String[CAPACIDAD_TOTAL];
    static LocalDateTime[] fechaIngreso = new LocalDateTime[CAPACIDAD_TOTAL];

    static int celdasOcupadas;
    static int celdasLibres;
    static long recaudoCarros;
    static long recaudoMotos;

    public static void main(String[] args) {
        inicializarArreglos();
        int opcion;
        do {
            mostrarMenu();
            System.out.print("Seleccione una opción: ");
            opcion = Integer.parseInt(SCANNER.nextLine().trim());
            ejecutarOpcion(opcion);
        } while (opcion != OPCION_SALIR);
        SCANNER.close();
    }
    static void mostrarMenu() {
        imprimir("");
        imprimir("===== PARQUEADERO =====");
        imprimir("1. Registrar entrada");
        imprimir("5. Salir");
    }
    static void ejecutarOpcion(int opcion) {
        switch (opcion) {
            case 1:
                registrarEntrada();
                break;
            case OPCION_SALIR:
                imprimir("Programa finalizado.");
                break;
            default:
                imprimir("Opción no disponible.");
        }
    }
    static void registrarEntrada() {
        int celda = -1;
        for (int i = 0; i < CAPACIDAD_TOTAL && celda == -1; i++) {
            if (tipoVehiculo[i] == null) {
                celda = i;
            }
        }
        if (celda == -1) {
            imprimir("El parqueadero está lleno. No es posible registrar el ingreso.");
            return;
        }

        System.out.print("Tipo de vehículo (1. Carro, 2. Moto): ");
        String tipo = SCANNER.nextLine().trim().equals("1") ? CARRO : MOTO;
        System.out.print("Fecha y hora de ingreso (yyyy-MM-dd HH:mm): ");
        LocalDateTime ingreso = LocalDateTime.parse(SCANNER.nextLine().trim(), FORMATO_FECHA_HORA);

        tipoVehiculo[celda] = tipo;
        fechaIngreso[celda] = ingreso;
        celdasOcupadas++;
        celdasLibres--;
        imprimir(tipo + " registrado en la celda " + (celda + 1) + ".");
    }
    static void imprimir(String mensaje) {
        System.out.println(mensaje);
    }
    static void inicializarArreglos() {
        for (int i = 0; i < CAPACIDAD_TOTAL; i++) {
            tipoVehiculo[i] = null;
            fechaIngreso[i] = null;
        }
        celdasOcupadas = 0;
        celdasLibres = CAPACIDAD_TOTAL;
        recaudoCarros = 0;
        recaudoMotos = 0;
    }
}
