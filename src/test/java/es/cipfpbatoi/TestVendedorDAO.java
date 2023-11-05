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
import es.cipfpbatoi.dao.VendedorDAO;
import es.cipfpbatoi.modelo.Vendedor;

@TestMethodOrder(OrderAnnotation.class)
class TestVendedorDAO {
	static VendedorDAO capaDao;
	Vendedor registroVacio = new Vendedor();
	Vendedor registroExiste1 = new Vendedor(1, "Carlos Zaltzmann", LocalDate.of(2015, 1, 1), 12000f);
	Vendedor registroExiste2 = new Vendedor(2, "Juan Fernandez", LocalDate.of(2014, 3, 1), 11500f);
	Vendedor registroNoExiste = new Vendedor(100, "No existe", LocalDate.of(0, 1, 1), 0f);
	Vendedor registroNuevo = new Vendedor("insert nombre test", LocalDate.of(2021, 5, 12), 10000f);
	Vendedor registroNuevoError = new Vendedor("insert nombre test 11111111111111111111111111111111",
			LocalDate.of(2021, 5, 12), 10000f);
	Vendedor registroModificarBorrar = new Vendedor(3, "update nombre test", LocalDate.of(2021, 5, 12), 10000f);
	Vendedor registroModificarBorrarError = new Vendedor(3, "update nombre test 11111111111111111111111111111111",
			LocalDate.of(2021, 5, 12), 10000f);
	static int numRegistrosEsperado = 3;
	static int autoIncrement = 3;
	final static String TABLA = "vendedores";
	final static String BD = "empresa_ad_test";

	@BeforeAll
	static void setUpBeforeClass() {
		try {
			capaDao = new VendedorDAO();

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
					+ "(nombre, fecha_ingreso, salario) values ('nombre test', '2021-05-12', 15000)");

		} catch (SQLException e) {
			fail("El test falla al preparar el test (instanciando dao: posiblemente falla la conexión a la BD)"
					+ e.getMessage());
		}
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		capaDao.cerrar();
	}

	@Test
	@Order(1)
	void testFind() {
		Vendedor registroObtenido;
		try {
			registroObtenido = capaDao.find(registroExiste1.getId());
			Vendedor registroEsperado = registroExiste1;
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
	@Order(2)
	void testInsert() {
		try {
			Vendedor registro = capaDao.insert(registroNuevo);
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
			assertEquals(numRegistrosEsperado + 2, registroNoExiste.getId());
		} catch (SQLException e) {
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
		} catch (SQLException e) {
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
		} catch (SQLException e) {
			fail("El testexists falla" + e.getMessage());
		}
	}

	@Test
	@Order(1)
	void testFindExample() {
		int numRegistrosObtenido;
		try {
			numRegistrosObtenido = capaDao.findByExample(registroVacio).size();
			assertEquals(numRegistrosEsperado, numRegistrosObtenido);
			numRegistrosObtenido = capaDao.findByExample(registroExiste1).size();
			assertEquals(1, numRegistrosObtenido);

			Vendedor registro = new Vendedor("los", null, 0f);
			numRegistrosObtenido = capaDao.findByExample(registro).size();
			assertEquals(1, numRegistrosObtenido);

			registro = new Vendedor("a", null, 0f);
			numRegistrosObtenido = capaDao.findByExample(registro).size();
			assertEquals(2, numRegistrosObtenido);

			registro = new Vendedor("NADA", LocalDate.of(2000, 1, 1), 1f);
			numRegistrosObtenido = capaDao.findByExample(registro).size();
			assertEquals(0, numRegistrosObtenido);

			registro = new Vendedor("a", LocalDate.of(2000, 1, 1), 1f);
			numRegistrosObtenido = capaDao.findByExample(registro).size();
			assertEquals(0, numRegistrosObtenido);

			registro = new Vendedor(null, LocalDate.of(2016, 1, 1), 0f);
			numRegistrosObtenido = capaDao.findByExample(registro).size();
			assertEquals(2, numRegistrosObtenido);

			registro = new Vendedor(null, null, 12000f);
			numRegistrosObtenido = capaDao.findByExample(registro).size();
			assertEquals(2, numRegistrosObtenido);
		} catch (SQLException e) {
			fail("El testfindexameple falla" + e.getMessage());
		}

	}

}
