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
            opcion = leerOpcion("Seleccione una opción: ", 1, OPCION_SALIR);
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
        int celda = celdaDisponible();
        if (celda == -1) {
            imprimir("El parqueadero está lleno. No es posible registrar el ingreso.");
            return;
        }

        String tipo = leerTipoVehiculo();
        LocalDateTime ingreso = leerFechaHora("Fecha y hora de ingreso (yyyy-MM-dd HH:mm): ");

        tipoVehiculo[celda] = tipo;
        fechaIngreso[celda] = ingreso;
        celdasOcupadas++;
        celdasLibres--;
        imprimir(tipo + " registrado en la celda " + (celda + 1) + ".");
    }
    static int celdaDisponible() {
        for (int i = 0; i < CAPACIDAD_TOTAL; i++) {
            if (tipoVehiculo[i] == null) {
                return i;
            }
        }
        return -1;
    }
    static int leerOpcion(String mensaje, int minimo, int maximo) {
        while (true) {
            System.out.print(mensaje);
            String texto = SCANNER.nextLine().trim();
            if (texto.length() >= 1 && texto.length() <= 9) {
                int valor = leerDigitos(texto, 0, texto.length());
                if (valor >= minimo && valor <= maximo) {
                    return valor;
                }
            }
            imprimir("Dato inválido. Ingrese un número entre " + minimo + " y " + maximo + ".");
        }
    }
    static String leerTipoVehiculo() {
        int opcion = leerOpcion("Tipo de vehículo (1. Carro, 2. Moto): ", 1, 2);
        return opcion == 1 ? CARRO : MOTO;
    }
    static LocalDateTime leerFechaHora(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = SCANNER.nextLine().trim();
            if (!formatoFechaHoraValido(texto)) {
                imprimir("Fecha u hora inválidas. Use yyyy-MM-dd HH:mm y el horario "
                        + String.format("%02d:00-%02d:00", HORA_APERTURA, HORA_CIERRE) + ".");
                continue;
            }

            LocalDateTime fecha = LocalDateTime.parse(texto, FORMATO_FECHA_HORA);

            if (!fecha.format(FORMATO_FECHA_HORA).equals(texto)) {
                imprimir("La fecha ingresada no existe en el calendario.");
                continue;
            }
            return fecha;
        }
    }
    static boolean formatoFechaHoraValido(String texto) {
        if (texto.length() != 16
                || texto.charAt(4) != '-'
                || texto.charAt(7) != '-'
                || texto.charAt(10) != ' '
                || texto.charAt(13) != ':') {
            return false;
        }

        int anio = leerDigitos(texto, 0, 4);
        int mes = leerDigitos(texto, 5, 2);
        int dia = leerDigitos(texto, 8, 2);
        int hora = leerDigitos(texto, 11, 2);
        int minuto = leerDigitos(texto, 14, 2);

        return anio >= 1 && mes >= 1 && mes <= 12 && dia >= 1 && dia <= 31
                && hora >= HORA_APERTURA && hora <= HORA_CIERRE
                && (hora != HORA_CIERRE || minuto <= 0)
                && minuto >= 0 && minuto <= 59;
    }
    static int leerDigitos(String texto, int inicio, int cantidad) {
        int valor = 0;
        for (int i = inicio; i < inicio + cantidad; i++) {
            char caracter = texto.charAt(i);
            if (caracter < '0' || caracter > '9') {
                return -1;
            }
            valor = valor * 10 + caracter - '0';
        }
        return valor;
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
