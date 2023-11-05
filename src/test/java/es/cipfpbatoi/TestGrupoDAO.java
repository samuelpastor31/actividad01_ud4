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

import es.cipfpbatoi.dao.ConexionBD;
import es.cipfpbatoi.dao  .GrupoDAO;
import es.cipfpbatoi.modelo.Grupo;

@TestMethodOrder(OrderAnnotation.class)
class TestGrupoDAO {
	static GrupoDAO capaDao;
	Grupo registroVacio = new Grupo();
	Grupo registroExiste1 = new Grupo(1, "Hardware");
	Grupo registroExiste2 = new Grupo(3, "Otros");
	Grupo registroNoExiste = new Grupo(100, "no existe");
	Grupo registroNuevo = new Grupo("insert test");
	Grupo registroNuevoError = new Grupo("insert test 12345");
	Grupo registroModificarBorrar = new Grupo(4, "update test");
	Grupo registroModificarBorrarError = new Grupo(4, "update test 12345");
	static int numRegistrosEsperado = 4;
	static int autoIncrement = 4;
	final static String TABLA = "grupos";
	final static String BD = "empresa_ad_test";

	@BeforeAll
	static void setUpBeforeClass() {
		try {
			capaDao = new GrupoDAO();

			ConexionBD.getConexion().createStatement()
					.executeUpdate("delete from " + BD + "." + TABLA + " where id >= " + numRegistrosEsperado);

			if (ConexionBD.getConexion().getMetaData().getDatabaseProductName().equals("MariaDB")) {
				ConexionBD.getConexion().createStatement()
						.executeUpdate("ALTER TABLE " + BD + "." + TABLA + " AUTO_INCREMENT = " + autoIncrement);
			} else { // PostgreSQL
				System.out.println("antes");
				ConexionBD.getConexion().createStatement()
						.executeUpdate("ALTER SEQUENCE " + BD + "." + TABLA + "_id_seq RESTART WITH " + autoIncrement);
				System.out.println("despues");
			}

			ConexionBD.getConexion().createStatement()
					.executeUpdate("insert into " + BD + ".grupos(descripcion) values ('descrip test')");

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
	void testfind() {
		Grupo registroObtenido;
		try {
			registroObtenido = capaDao.find(registroExiste1.getId());
			Grupo registroEsperado = registroExiste1;
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
			Grupo registro = capaDao.insert(registroNuevo);
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

}
