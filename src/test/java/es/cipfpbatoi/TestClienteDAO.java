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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import es.cipfpbatoi.dao.ClienteDAO;
import es.cipfpbatoi.dao.ConexionBD;
import es.cipfpbatoi.modelo.Cliente;

@TestMethodOrder(OrderAnnotation.class)
class TestClienteDAO {
	static ClienteDAO capaDao;
	Cliente registroVacio = new Cliente();
	Cliente registroExiste1 = new Cliente(1, "Matt Design", "C/ Pintor Sorolla, 3");
	Cliente registroExiste2 = new Cliente(4, "Luis Llull", "C/ Salvador Dalí, 3");
	Cliente registroNoExiste = new Cliente(100, "no existe", "no existe");
	Cliente registroNuevo = new Cliente("insert nombre test", "insert direccion test");
	Cliente registroNuevoError = new Cliente("insert nombre test 111111111111111111111111111111111111111111",
			"insert direccion test");
	Cliente registroModificarBorrar = new Cliente(5, "update nombre test", "update direccion test");
	Cliente registroModificarBorrarError = new Cliente(5,
			"update nombre test 111111111111111111111111111111111111111111", "update direccion test");
	static int numRegistrosEsperado = 5;
	static int autoIncrement = 5;
	final static String TABLA = "clientes";
	final static String BD = "empresa_ad_test";

	@BeforeAll
	static void setUpBeforeClass() {
		try {
			capaDao = new ClienteDAO();

			ConexionBD.getConexion().createStatement()
					.executeUpdate("delete from " + BD + "." + TABLA + " where id >= " + numRegistrosEsperado);

			if (ConexionBD.getConexion().getMetaData().getDatabaseProductName().equals("MariaDB")) {
				ConexionBD.getConexion().createStatement()
						.executeUpdate("ALTER TABLE " + BD + "." + TABLA + " AUTO_INCREMENT = " + autoIncrement);
			} else { // PostgreSQL
				System.out.println("antes");
				ConexionBD.getConexion().createStatement()
						.executeUpdate("ALTER SEQUENCE " + BD + "." + TABLA + "_id_seq RESTART WITH " + autoIncrement);
			}

			ConexionBD.getConexion().createStatement().executeUpdate(
					"insert into " + BD + "." + TABLA + "(nombre, direccion) values ('nombre test', 'direccion test')");

		} catch (SQLException e) {
			fail("El test falla al preparar el test (instanciando dao: posiblemente falla la conexión a la BD)"+e.getMessage());
		}
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		capaDao.cerrar();
	}

	@Test
	@Order(1)
	void testFind() {
		Cliente registroObtenido;
		try {
			registroObtenido = capaDao.find(registroExiste1.getId());
			Cliente registroEsperado = registroExiste1;
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
			Cliente registro = capaDao.insert(registroNuevo);
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
	@Order(1)
	void testFindExample() {
		int numRegistrosObtenido;
		try {
			numRegistrosObtenido = capaDao.findByExample(registroVacio).size();
			assertEquals(numRegistrosEsperado, numRegistrosObtenido);

			numRegistrosObtenido = capaDao.findByExample(registroExiste1).size();
			assertEquals(1, numRegistrosObtenido);

			Cliente registro = new Cliente("ana", null);
			numRegistrosObtenido = capaDao.findByExample(registro).size();
			assertEquals(1, numRegistrosObtenido);

			registro = new Cliente(null, "C/");
			numRegistrosObtenido = capaDao.findByExample(registro).size();
			assertEquals(3, numRegistrosObtenido);

			registro = new Cliente("NADA", "NADA");
			numRegistrosObtenido = capaDao.findByExample(registro).size();
			assertEquals(0, numRegistrosObtenido);
		} catch (SQLException e) {
			fail("El testfindexameple falla" + e.getMessage());
		}
		
	}

}
