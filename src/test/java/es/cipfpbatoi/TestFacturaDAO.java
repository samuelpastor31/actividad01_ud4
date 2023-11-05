package es.cipfpbatoi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.SQLException;
import java.time.LocalDate;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import es.cipfpbatoi.dao.ConexionBD;
import es.cipfpbatoi.dao.FacturaDAO;
import es.cipfpbatoi.modelo.Cliente;
import es.cipfpbatoi.modelo.Factura;
import es.cipfpbatoi.modelo.Vendedor;

@TestMethodOrder(OrderAnnotation.class)
class TestFacturaDAO {
	static FacturaDAO capaDao;
	Factura registroVacio = new Factura();
	Cliente cli2 = new Cliente(2, "Diana Perez", "C/ Brito del Pino, 1120");
	Vendedor ven2 = new Vendedor(2, "Juan Fernandez", LocalDate.of(2014, 3, 1), 11500f);

	Factura registroExiste1 = new Factura(2, LocalDate.of(2008, 3, 18), 2, 2, "Contado");
	Factura registroExiste2 = new Factura(17, LocalDate.of(2012, 10, 11), 2, 2, "transferencia");
	Factura registroNoExiste = new Factura(10000, LocalDate.of(0, 1, 1), 2, 2, "Contado");

	Factura registroNuevo = new Factura(LocalDate.of(2021, 12, 1), 2, 2, "tarjeta");
	Factura registroNuevoError = new Factura(LocalDate.of(2021, 12, 1), 100, 2, "tarjeta");
	Factura registroModificarBorrar = new Factura(5000, LocalDate.of(2021, 12, 2), 2, 2, "Contado");
	Factura registroModificarBorrarError = new Factura(5000, LocalDate.of(2021, 12, 2), 100, 2, "Contado");

	static int numRegistrosEsperado = 5000;
	static int autoIncrement = 5000;
	final static String TABLA = "facturas";
	final static String BD = "empresa_ad_test";

	@BeforeAll
	static void setUpBeforeClass() {
		try {
			capaDao = new FacturaDAO();

			ConexionBD.getConexion().createStatement()
					.executeUpdate("delete from " + BD + "." + TABLA + " where id >= " + numRegistrosEsperado);

			if (ConexionBD.getConexion().getMetaData().getDatabaseProductName().equals("MariaDB")) {
				ConexionBD.getConexion().createStatement()
						.executeUpdate("ALTER TABLE " + BD + "." + TABLA + " AUTO_INCREMENT = " + autoIncrement);
			} else { // PostgreSQL
				ConexionBD.getConexion().createStatement()
						.executeUpdate("ALTER SEQUENCE " + BD + "." + TABLA + "_id_seq RESTART WITH " + autoIncrement);
			}

			ConexionBD.getConexion().createStatement().executeUpdate("insert into " + BD + "." + TABLA
					+ "(fecha, cliente, vendedor, formapago) values ('2021-11-30', 4, 1, 'Contado')");

		} catch (SQLException e) {
			fail("El test falla al preparar el test (instanciando dao: posiblemente falla la conexión a la BD)"
					+ e.getMessage());
		}
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		capaDao.cerrar();
	}

//	@BeforeEach
//	static void setUp() {
//		try {
//			ConexionBD.getConexion().createStatement().executeUpdate("delete from empresa_ad.clientes where id > 5");
//		} catch (SQLException e) {
//			fail("El test falla en la preparación antes de cada test (preparando tabla clientes)");
//		}
//	}

	@Test
	@Order(1)
	void testfind() {
		Factura registroObtenido;
		try {
			registroObtenido = capaDao.find(registroExiste1.getId());
			Factura registroEsperado = registroExiste1;
			assertEquals(registroEsperado, registroObtenido);
			registroObtenido = capaDao.find(registroExiste2.getId());
			registroEsperado = registroExiste2;
			assertEquals(registroEsperado, registroObtenido);
			registroObtenido = capaDao.find(registroNoExiste.getId());
			assertNull(registroObtenido);
		} catch (SQLException e) {
			fail("El testfind falla" + e.getMessage());
		}
	}

	@Test
	@Order(1)
	void testFindAll() {
		int numRegistrosObtenido;
		try {
			numRegistrosObtenido = capaDao.findAll().size();
			assertEquals(numRegistrosEsperado, numRegistrosObtenido);
		} catch (SQLException e) {
			fail("El testfindall falla" + e.getMessage());
		}
	}

	@Test
	@Order(1)
	void testFindByCliente() {
		int numRegistrosObtenido;
		try {
			numRegistrosObtenido = capaDao.findByCliente(1000).size();
			assertEquals(0, numRegistrosObtenido);

			numRegistrosObtenido = capaDao.findByCliente(1).size();
			assertEquals(1656, numRegistrosObtenido);

			numRegistrosObtenido = capaDao.findByCliente(2).size();
			assertEquals(1662, numRegistrosObtenido);
		} catch (SQLException e) {
			fail("El testfindcliente falla" + e.getMessage());
		}
	}

	@Test
	@Order(1)
	void testFindByVendedor() {
		int numRegistrosObtenido;
		try {
			numRegistrosObtenido = capaDao.findByVendedor(1000).size();
			assertEquals(0, numRegistrosObtenido);

			numRegistrosObtenido = capaDao.findByVendedor(1).size();
			assertEquals(2470, numRegistrosObtenido);

			numRegistrosObtenido = capaDao.findByVendedor(2).size();
			assertEquals(2530, numRegistrosObtenido);
		} catch (SQLException e) {
			fail("El testfindvendedor falla" + e.getMessage());
		}
	}

	@Test
	@Order(2)
	void testInsert() {
		try {
			Factura registro = capaDao.insert(registroNuevo);
			assertNotNull(registro);
			assertNotEquals(0, registro.getId());
			assertEquals(numRegistrosEsperado + 1, registro.getId());
		} catch (SQLException e) {
			fail("El testinsert falla" + e.getMessage());
		}

		Exception ex = assertThrows(SQLException.class, () -> {
			capaDao.insert(registroNuevoError);
		});
		assertTrue(!ex.getMessage().isEmpty());
	}

	@Test
	@Order(3)
	void testUpdate() {
		boolean respuestaObtenida;
		try {
			respuestaObtenida = capaDao.update(registroModificarBorrar);
			assertTrue(respuestaObtenida);
			respuestaObtenida = capaDao.update(registroNoExiste);
			assertFalse(respuestaObtenida);
		} catch (SQLException e) {
			fail("El testupdate falla" + e.getMessage());
		}

		Exception ex = assertThrows(SQLException.class, () -> {
			capaDao.update(registroModificarBorrarError);
		});
		assertTrue(!ex.getMessage().isEmpty());
	}

	@Test
	@Order(4)
	void testSave() {
		Boolean respuestaObtenida;
		try {
			respuestaObtenida = capaDao.save(registroModificarBorrar);
			assertTrue(respuestaObtenida);
			respuestaObtenida = capaDao.save(registroNoExiste);
			assertTrue(respuestaObtenida);
			assertNotEquals(0, registroNoExiste.getId());
			assertEquals(numRegistrosEsperado + 3, registroNoExiste.getId());
		} catch (Exception e) {
			fail("Falla testSave" + e.getMessage());
		}

		Exception ex = assertThrows(SQLException.class, () -> {
			capaDao.save(registroModificarBorrarError);
		});
		assertTrue(!ex.getMessage().isEmpty());
	}

	@Test
	@Order(5)
	void testDelete() {
		boolean respuestaObtenida;
		try {
			respuestaObtenida = capaDao.delete(registroModificarBorrar);
			assertTrue(respuestaObtenida);
			respuestaObtenida = capaDao.delete(registroNoExiste);
			assertFalse(respuestaObtenida);
		} catch (SQLException e) {
			fail("El testdelete falla" + e.getMessage());
		}
	}

	@Test
	@Order(1)
	void testSize() {
		long respuestaObtenida;
		try {
			respuestaObtenida = capaDao.size();
			assertEquals(numRegistrosEsperado, respuestaObtenida);
		} catch (Exception e) {
			fail("El testsize falla" + e.getMessage());
		}
	}

	@Test
	@Order(6)
	void testExists() {
		boolean respuestaObtenida;
		try {
			respuestaObtenida = capaDao.exists(registroExiste1.getId());
			assertTrue(respuestaObtenida);
			respuestaObtenida = capaDao.exists(registroNoExiste.getId());
			assertFalse(respuestaObtenida);
		} catch (Exception e) {
			fail("El testexists falla" + e.getMessage());
		}
	}

	@Test
	@Order(7)
	void testGetNextLine() {
		int respuestaObtenida;
		try {
			respuestaObtenida = capaDao.getNextLine(registroExiste1.getId());
			assertEquals(5, respuestaObtenida);
			respuestaObtenida = capaDao.getNextLine(1);
			assertEquals(3, respuestaObtenida);
			respuestaObtenida = capaDao.getNextLine(numRegistrosEsperado + 1); // factura sin lineas
			assertEquals(1, respuestaObtenida);
			respuestaObtenida = capaDao.getNextLine(registroNoExiste.getId());
			assertEquals(-1, respuestaObtenida);
		} catch (SQLException e) {
			fail("El textgetnextline falla" + e.getMessage());
		}
	}

	@Test
	@Order(8)
	void testGetImporteTotal() {
		double respuestaObtenida;
		try {
			respuestaObtenida = capaDao.getImporteTotal(registroExiste1.getId());
			assertEquals(112, respuestaObtenida);
			respuestaObtenida = capaDao.getImporteTotal(registroExiste2.getId());
			assertEquals(4308, respuestaObtenida);
			respuestaObtenida = capaDao.getImporteTotal(registroNoExiste.getId());
			assertEquals(-1, respuestaObtenida);
		} catch (SQLException e) {
			fail("El textgetimportetotal falla" + e.getMessage());
		}
		
	}

	@Test
	@Order(9)
	void testFindByExample() throws SQLException {
		assertNull(capaDao.findByExample(registroExiste1));
	}

}
