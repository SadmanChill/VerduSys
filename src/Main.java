import java.sql.*;
import java.util.Scanner;

public class Main {
    
    // CONFIGURACIÓN DE LA BASE DE DATOS
    static final String URL = "jdbc:mysql://localhost:3306/verdusys";
    static final String USUARIO = "root";
    static final String CONTRASENA = "juanjosE45."; 
    
    static Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        try {
            // Cargar driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Probar conexión
            Connection conn = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
            System.out.println("✅ Conectado a MySQL");
            conn.close();
            
            int opcion;
            do {
                mostrarMenu();
                opcion = leerEntero();
                
                switch (opcion) {
                    case 1: agregarVerdura(); break;
                    case 2: listarVerduras(); break;
                    case 3: buscarVerdura(); break;
                    case 4: actualizarVerdura(); break;
                    case 5: eliminarVerdura(); break;
                    case 6: stockBajo(); break;
                    case 7: System.out.println("👋 Hasta luego"); break;
                    default: System.out.println("❌ Opción inválida");
                }
                
                if (opcion != 7) {
                    System.out.println("\nPresione Enter para continuar...");
                    scanner.nextLine();
                }
                
            } while (opcion != 7);
            
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Error: No se encontró el driver MySQL");
            System.out.println("📌 Solución: Agrega mysql-connector-java.jar a lib/");
        } catch (SQLException e) {
            System.out.println("❌ Error de conexión: " + e.getMessage());
            System.out.println("📌 Solución: Verifica que MySQL esté corriendo y la contraseña sea correcta");
        }
    }
    
    static void mostrarMenu() {
        System.out.println("\n=== VERDUSYS ===");
        System.out.println("1. Agregar verdura");
        System.out.println("2. Listar todas");
        System.out.println("3. Buscar por ID");
        System.out.println("4. Actualizar verdura");
        System.out.println("5. Eliminar verdura");
        System.out.println("6. Ver stock bajo");
        System.out.println("7. Salir");
        System.out.print("Opción: ");
    }
    
    static void agregarVerdura() {
        System.out.println("\n--- AGREGAR VERDURA ---");
        
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        
        System.out.print("Cantidad: ");
        int cantidad = leerEntero();
        
        System.out.print("Precio: ");
        double precio = leerDouble();
        
        System.out.print("Proveedor: ");
        String proveedor = scanner.nextLine();
        
        String sql = "INSERT INTO verduras (nombre, cantidad, precio_unitario, proveedor, fecha_ingreso, stock_minimo) VALUES (?, ?, ?, ?, CURDATE(), 10)";
        
        try (Connection conn = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nombre);
            stmt.setInt(2, cantidad);
            stmt.setDouble(3, precio);
            stmt.setString(4, proveedor);
            
            int resultado = stmt.executeUpdate();
            
            if (resultado > 0) {
                System.out.println("✅ Verdura agregada correctamente");
            } else {
                System.out.println("❌ Error al agregar");
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
    
    static void listarVerduras() {
        System.out.println("\n--- LISTA DE VERDURAS ---");
        
        String sql = "SELECT * FROM verduras ORDER BY nombre";
        
        try (Connection conn = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            boolean hayDatos = false;
            
            while (rs.next()) {
                hayDatos = true;
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Nombre: " + rs.getString("nombre"));
                System.out.println("Cantidad: " + rs.getInt("cantidad"));
                System.out.println("Precio: $" + rs.getDouble("precio_unitario"));
                System.out.println("Proveedor: " + rs.getString("proveedor"));
                System.out.println("------------------------");
            }
            
            if (!hayDatos) {
                System.out.println("No hay verduras registradas");
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
    
    static void buscarVerdura() {
        System.out.print("\nID de la verdura: ");
        int id = leerEntero();
        
        String sql = "SELECT * FROM verduras WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                System.out.println("\n📦 VERDURA ENCONTRADA:");
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Nombre: " + rs.getString("nombre"));
                System.out.println("Cantidad: " + rs.getInt("cantidad"));
                System.out.println("Precio: $" + rs.getDouble("precio_unitario"));
                System.out.println("Proveedor: " + rs.getString("proveedor"));
                
                // Verificar stock bajo
                if (rs.getInt("cantidad") <= 10) {
                    System.out.println("⚠️ ¡STOCK BAJO!");
                }
            } else {
                System.out.println("❌ No existe verdura con ID " + id);
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
    
    static void actualizarVerdura() {
        System.out.print("\nID de la verdura a actualizar: ");
        int id = leerEntero();
        
        // Primero verificamos si existe
        String sqlBuscar = "SELECT * FROM verduras WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
             PreparedStatement stmtBuscar = conn.prepareStatement(sqlBuscar)) {
            
            stmtBuscar.setInt(1, id);
            ResultSet rs = stmtBuscar.executeQuery();
            
            if (!rs.next()) {
                System.out.println("❌ No existe verdura con ID " + id);
                return;
            }
            
            // Mostrar datos actuales
            System.out.println("\nDatos actuales:");
            System.out.println("Nombre: " + rs.getString("nombre"));
            System.out.println("Cantidad: " + rs.getInt("cantidad"));
            System.out.println("Precio: $" + rs.getDouble("precio_unitario"));
            System.out.println("Proveedor: " + rs.getString("proveedor"));
            
            // Pedir nuevos datos
            System.out.println("\nIngrese los nuevos datos (Enter para mantener actual):");
            
            System.out.print("Nuevo nombre: ");
            String nombre = scanner.nextLine();
            
            System.out.print("Nueva cantidad: ");
            String cantStr = scanner.nextLine();
            
            System.out.print("Nuevo precio: ");
            String precioStr = scanner.nextLine();
            
            System.out.print("Nuevo proveedor: ");
            String proveedor = scanner.nextLine();
            
            // Construir SQL de actualización
            String sqlUpdate = "UPDATE verduras SET nombre = ?, cantidad = ?, precio_unitario = ?, proveedor = ? WHERE id = ?";
            
            try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)) {
                
                stmtUpdate.setString(1, nombre.isEmpty() ? rs.getString("nombre") : nombre);
                stmtUpdate.setInt(2, cantStr.isEmpty() ? rs.getInt("cantidad") : Integer.parseInt(cantStr));
                stmtUpdate.setDouble(3, precioStr.isEmpty() ? rs.getDouble("precio_unitario") : Double.parseDouble(precioStr));
                stmtUpdate.setString(4, proveedor.isEmpty() ? rs.getString("proveedor") : proveedor);
                stmtUpdate.setInt(5, id);
                
                int resultado = stmtUpdate.executeUpdate();
                
                if (resultado > 0) {
                    System.out.println("✅ Verdura actualizada");
                } else {
                    System.out.println("❌ Error al actualizar");
                }
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("❌ Error: Ingrese números válidos");
        }
    }
    
    static void eliminarVerdura() {
        System.out.print("\nID de la verdura a eliminar: ");
        int id = leerEntero();
        
        System.out.print("¿Está seguro? (S/N): ");
        String confirmacion = scanner.nextLine();
        
        if (confirmacion.equalsIgnoreCase("S")) {
            String sql = "DELETE FROM verduras WHERE id = ?";
            
            try (Connection conn = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setInt(1, id);
                int resultado = stmt.executeUpdate();
                
                if (resultado > 0) {
                    System.out.println("✅ Verdura eliminada");
                } else {
                    System.out.println("❌ No existe verdura con ID " + id);
                }
                
            } catch (SQLException e) {
                System.out.println("❌ Error: " + e.getMessage());
            }
        } else {
            System.out.println("Operación cancelada");
        }
    }
    
    static void stockBajo() {
        System.out.println("\n--- VERDURAS CON STOCK BAJO (≤10 unidades) ---");
        
        String sql = "SELECT * FROM verduras WHERE cantidad <= 10 ORDER BY cantidad ASC";
        
        try (Connection conn = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            boolean hayStockBajo = false;
            
            while (rs.next()) {
                hayStockBajo = true;
                System.out.println("ID: " + rs.getInt("id") + 
                                 " | " + rs.getString("nombre") + 
                                 " | Stock: " + rs.getInt("cantidad") + 
                                 " | Mínimo: 10 ⚠️");
            }
            
            if (!hayStockBajo) {
                System.out.println("✅ Todo bien, no hay stock bajo");
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
    
    // Métodos auxiliares
    static int leerEntero() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    static double leerDouble() {
        try {
            return Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}