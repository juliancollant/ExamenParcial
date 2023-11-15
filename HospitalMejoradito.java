import java.sql.*;
import java.util.Scanner;

public class Hospital {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "juliancollant", "pancuca1")) {
            System.out.println("Conexión exitosa a la base de datos");

            boolean continuar = true;
            while (continuar) {
                System.out.println("Menú:");
                System.out.println("1. Mostrar pacientes");
                System.out.println("2. Mostrar doctores");
                System.out.println("3. Agregar pacientes");
                System.out.println("4. Agregar doctores");
                System.out.println("5. Eliminar paciente por nombre");
                System.out.println("6. Despedir doctor");
                System.out.println("7. Salir");
                System.out.println("Seleccione una opción:");

                int opcionMenu = scanner.nextInt();
                scanner.nextLine();

                switch (opcionMenu) {
                    case 1:
                        mostrarPacientes(connection);
                        break;
                    case 2:
                        mostrarDoctores(connection);
                        break;
                    case 3:
                        agregarPaciente(connection, scanner);
                        break;
                    case 4:
                        agregarDoctor(connection, scanner);
                        break;
                    case 5:   eliminarPacientePorNombre(connection, scanner);
                        break;
                    case 6:
                        despedirDoctor(connection, scanner);
                        break;
                    case 7:
                        continuar = false;
                        break;
                    default:
                        System.out.println("Opción inválida");
                        break;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al conectar a la base de datos: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    public static void mostrarPacientes(Connection connection) {
        try {
            String consulta = "SELECT * FROM pacientes";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(consulta);

            System.out.println("Listado de Pacientes:");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nombre = resultSet.getString("nombre");
                int edad = resultSet.getInt("edad");
                String fechaIngreso = resultSet.getString("fecha_ingreso");
                String doctor = resultSet.getString("doctor");
                String sintomasMedicos = resultSet.getString("sintomasMedicos");

                System.out.println("ID: " + id + ", Nombre: " + nombre + ", Edad: " + edad +
                        ", Fecha de Ingreso: " + fechaIngreso + ", Doctor: " + doctor +
                        ", Síntomas: " + sintomasMedicos);
            }

            statement.close();
        } catch (SQLException e) {
            System.out.println("Error al mostrar pacientes: " + e.getMessage());
        }
    }

    public static void mostrarDoctores(Connection connection) {
        try {
            String consulta = "SELECT * FROM doctores";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(consulta);

            System.out.println("Listado de Doctores:");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nombre = resultSet.getString("nombre");
                String especialidad = resultSet.getString("especialidad");

                System.out.println("ID: " + id + ", Nombre: " + nombre+"Especialidad : "+especialidad);
            }

            statement.close();
        } catch (SQLException e) {
            System.out.println("Error al mostrar doctores: " + e.getMessage());
        }
    }

    public static void agregarPaciente(Connection connection, Scanner scanner) {
        try {
            System.out.println("Ingrese el ID del paciente:");
            int id = scanner.nextInt();
            scanner.nextLine(); // Consumir el salto de línea

            System.out.println("Ingrese el nombre del paciente:");
            String nombre = scanner.nextLine();

            System.out.println("Ingrese la edad del paciente:");
            int edad = scanner.nextInt();
            scanner.nextLine(); // Consumir el salto de línea

            System.out.println("Ingrese los síntomas del paciente:");
            String sintomasMedicos = scanner.nextLine();

            System.out.println("Ingrese la fecha de ingreso del paciente (YYYY-MM-DD):");
            String fechaIngreso = scanner.nextLine();

            mostrarDoctores(connection);

            System.out.println("Ingrese el ID del doctor del paciente:");
            int idDoctor = scanner.nextInt();
            scanner.nextLine(); // Consumir el salto de línea

            if (verificarDoctorExistente(connection, idDoctor)) {
                String insertPaciente = "INSERT INTO pacientes (id, nombre, edad, sintomasMedicos, fecha_ingreso, doctor) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(insertPaciente);

                preparedStatement.setInt(1, id);
                preparedStatement.setString(2, nombre);
                preparedStatement.setInt(3, edad);
                preparedStatement.setString(4, sintomasMedicos);
                preparedStatement.setString(5, fechaIngreso);
                preparedStatement.setInt(6, idDoctor);

                int filasInsertadas = preparedStatement.executeUpdate();

                if (filasInsertadas > 0) {
                    System.out.println("Nuevo paciente agregado correctamente a la base de datos.");
                } else {
                    System.out.println("No se pudo agregar el paciente.");
                }
            } else {
                System.out.println("El doctor seleccionado no existe en la base de datos.");
            }
        } catch (SQLException e) {
            System.out.println("Error al agregar paciente: " + e.getMessage());
        }
    }
    public static void agregarDoctor(Connection connection, Scanner scanner) {
        try {
            System.out.println("Ingrese el ID del doctor:");
            int id = scanner.nextInt();
            scanner.nextLine(); // Consumir el salto de línea

            System.out.println("Ingrese el nombre del doctor:");
            String nombre = scanner.nextLine();

            System.out.println("Ingrese Especialidad del doctor:");
            String especialidad= scanner.nextLine();

            String insertDoctor = "INSERT INTO doctores (id, nombre,especialidad) VALUES (?, ?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertDoctor);

            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, nombre);
            preparedStatement.setString(3,especialidad);

            int filasInsertadas = preparedStatement.executeUpdate();

            if (filasInsertadas > 0) {
                System.out.println("Nuevo doctor agregado correctamente a la base de datos.");
            } else {
                System.out.println("No se pudo agregar el doctor.");
            }
        } catch (SQLException e) {
            System.out.println("Error al agregar doctor: " + e.getMessage());
        }
    }

    public static boolean verificarDoctorExistente(Connection connection, int idDoctor) throws SQLException {
        String consulta = "SELECT * FROM doctores WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(consulta);
        preparedStatement.setInt(1, idDoctor);
        ResultSet resultSet = preparedStatement.executeQuery();

        return resultSet.next();
    }

    public static void eliminarPacientePorNombre(Connection connection, Scanner scanner) {
        try {
            System.out.println("Ingrese el nombre del paciente que desea eliminar:");
            String nombrePaciente = scanner.nextLine();

            String deletePaciente = "DELETE FROM pacientes WHERE nombre = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(deletePaciente);
            preparedStatement.setString(1, nombrePaciente);

            int filasEliminadas = preparedStatement.executeUpdate();

            if (filasEliminadas > 0) {
                System.out.println("Paciente eliminado correctamente de la base de datos.");
            } else {
                System.out.println("No se encontró ningún paciente con ese nombre.");
            }
        } catch (SQLException e) {
            System.out.println("Error al eliminar paciente por nombre: " + e.getMessage());
        }
    }

    public static void despedirDoctor(Connection connection, Scanner scanner) {
        try {
            System.out.println("Ingrese el ID del doctor que desea despedir:");
            int idDoctor = scanner.nextInt();
            scanner.nextLine(); // Consumir el salto de línea

            String deleteDoctor = "DELETE FROM doctores WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(deleteDoctor);

            preparedStatement.setInt(1, idDoctor);

            int filasEliminadas = preparedStatement.executeUpdate();

            if (filasEliminadas > 0) {
                System.out.println("Doctor despedido correctamente de la base de datos.");
            } else {
                System.out.println("No se encontró ningún doctor con ese ID.");
            }
        } catch (SQLException e) {
            System.out.println("Error al despedir doctor: " + e.getMessage());
        }
    }
}

