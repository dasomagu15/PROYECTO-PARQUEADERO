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
        imprimir("2. Registrar salida");
        imprimir("3. Reporte de ocupación");
        imprimir("4. Reporte financiero");
        imprimir("5. Salir");
    }

    static void ejecutarOpcion(int opcion) {
        switch (opcion) {
            case 1:
                registrarEntrada();
                break;
            case 2:
                registrarSalida();
                break;
            case 3:
                reporteOcupacion();
                break;
            case 4:
                reporteFinanciero();
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
    static void registrarSalida() {
        if (celdasOcupadas == 0) {
            imprimir("No hay vehículos en el parqueadero.");
            return;
        }

        int celda = leerCelda();
        if (tipoVehiculo[celda] == null) {
            imprimir("La celda " + (celda + 1) + " está libre. No hay un vehículo para liquidar.");
            return;
        }

        String tipo = tipoVehiculo[celda];
        LocalDateTime ingreso = fechaIngreso[celda];
        LocalDateTime salida = leerFechaHora("Fecha y hora de salida (yyyy-MM-dd HH:mm): ");
        while (salida.isBefore(ingreso)) {
            imprimir("La salida no puede ser anterior al ingreso (" + ingreso.format(FORMATO_FECHA_HORA) + ").");
            salida = leerFechaHora("Fecha y hora de salida (yyyy-MM-dd HH:mm): ");
        }

        long valor = liquidar(celda, salida);
        if (tipo.equals(CARRO)) {
            recaudoCarros += valor;
        } else {
            recaudoMotos += valor;
        }

        tipoVehiculo[celda] = null;
        fechaIngreso[celda] = null;
        celdasOcupadas--;
        celdasLibres++;
    }
    static long liquidar(int celda, LocalDateTime salida) {
        String tipo = tipoVehiculo[celda];
        LocalDateTime ingreso = fechaIngreso[celda];

        long diasACobrar = contarDiasCompletos(ingreso, salida);
        LocalDateTime cursor = ingreso.plusDays(diasACobrar);
        long horasACobrar = calcularHorasRestantes(cursor, salida);
        if (horasACobrar >= UMBRAL_HORAS_DIA) {
            diasACobrar++;
            horasACobrar = 0;
        }

        long tarifaDia = obtenerTarifaDia(tipo);
        long tarifaHora = obtenerTarifaHora(tipo);
        long total = diasACobrar * tarifaDia + horasACobrar * tarifaHora;

        imprimirFactura(celda, tipo, ingreso, salida, diasACobrar, tarifaDia, horasACobrar, tarifaHora, total);
        return total;
    }
    static long contarDiasCompletos(LocalDateTime ingreso, LocalDateTime salida) {
        return ChronoUnit.DAYS.between(ingreso, salida);
    }
    static long calcularHorasRestantes(LocalDateTime cursor, LocalDateTime salida) {
        long minutosRestantes = Duration.between(cursor, salida).toMinutes();
        return (minutosRestantes + 59) / 60;
    }

    static long obtenerTarifaDia(String tipo) {
        return tipo.equals(CARRO) ? TARIFA_DIA_CARRO : TARIFA_DIA_MOTO;
    }

    static long obtenerTarifaHora(String tipo) {
        return tipo.equals(CARRO) ? TARIFA_HORA_CARRO : TARIFA_HORA_MOTO;
    }

    static void imprimirFactura(int celda, String tipo, LocalDateTime ingreso, LocalDateTime salida,
                                long dias, long tarifaDia, long horas, long tarifaHora, long total) {
        imprimir("");
        imprimir("========== FACTURA ==========");
        imprimir("Celda liberada : " + (celda + 1));
        imprimir("Vehículo       : " + tipo);
        imprimir("Ingreso        : " + ingreso.format(FORMATO_FECHA_HORA));
        imprimir("Salida         : " + salida.format(FORMATO_FECHA_HORA));
        if (dias > 0) {
            imprimir("Días           : " + dias + " x " + formatoMoneda(tarifaDia)
                    + " = " + formatoMoneda(dias * tarifaDia));
        }
        if (horas > 0) {
            imprimir("Horas          : " + horas + " x " + formatoMoneda(tarifaHora)
                    + " = " + formatoMoneda(horas * tarifaHora));
        }
        imprimir("TOTAL A PAGAR  : " + formatoMoneda(total));
        imprimir("=============================");
    }

    static String formatoMoneda(long valor) {
        return "$" + String.format("%,d", valor);
    }

    // ---------- Reportes ----------
    static void reporteOcupacion() {
        imprimir("");
        imprimir("----- Reporte de ocupación -----");
        imprimir("Celdas ocupadas: " + celdasOcupadas);
        imprimir("Celdas libres  : " + celdasLibres);
    }

    static void reporteFinanciero() {
        imprimir("");
        imprimir("----- Reporte financiero -----");
        imprimir("Recaudo carros : " + formatoMoneda(recaudoCarros));
        imprimir("Recaudo motos  : " + formatoMoneda(recaudoMotos));
        imprimir("Total general  : " + formatoMoneda(recaudoCarros + recaudoMotos));
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
    static int leerCelda() {
        return leerOpcion("Número de celda (1-" + CAPACIDAD_TOTAL + "): ", 1, CAPACIDAD_TOTAL) - 1;
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