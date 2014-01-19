package foo.domain.export;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

public class HibernateSchemaGenerator {

	private Configuration cfg;

	public static void main(String[] args) throws Exception {
		HibernateSchemaGenerator gen = new HibernateSchemaGenerator();
		gen.runExport();
	}

	/**
	 * @param args
	 */
	public void runExport() throws Exception {
		System.out.println("--=========== Start SchemaGenerator ============");
		this.init();
		this.initPackages("foo.domain.entity");
		this.generate(Dialect.ORACLE);
		// gen.generate(Dialect.MYSQL);
		// gen.generate(Dialect.HSQL);
		System.out.println("--=========== END SchemaGenerator ===========");
	}

	public void init() throws Exception {
		cfg = new Configuration();
		cfg.setProperty("hibernate.hbm2ddl.auto", "create");
		// strategy
	}

	public void initPackages(String packageName) throws Exception {
		for (Class<Object> clazz : getClasses(packageName)) {
			cfg.addAnnotatedClass(clazz);
		}
	}

	/**
	 * Method that actually creates the file.
	 * 
	 * @param dbDialect
	 *            to use
	 */
	private void generate(Dialect dialect) {
		cfg.setProperty("hibernate.dialect", dialect.getDialectClass());

		SchemaExport export = new SchemaExport(cfg);
		export.setDelimiter(";");
		export.setOutputFile("ddl_" + dialect.name().toLowerCase() + ".sql");
		export.execute(true, false, false, false);
	}

	/**
	 * Utility method used to fetch Class list based on a package name.
	 * 
	 * @param packageName
	 *            (should be the package containing your annotated beans.
	 */
	private List<Class> getClasses(String packageName) throws Exception {
		List<Class> classes = new ArrayList<Class>();
		File directory = null;
		try {
			ClassLoader cld = Thread.currentThread().getContextClassLoader();
			if (cld == null) {
				throw new ClassNotFoundException("Can't get class loader.");
			}
			String path = packageName.replace('.', '/');
			URL resource = cld.getResource(path);
			if (resource == null) {
				throw new ClassNotFoundException("No resource for " + path);
			}
			directory = new File(resource.getFile());
		} catch (NullPointerException x) {
			throw new ClassNotFoundException(packageName + " (" + directory + ") does not appear to be a valid package");
		}
		if (directory.exists()) {
			String[] files = directory.list();
			for (int i = 0; i < files.length; i++) {
				if (files[i].endsWith(".class") && !files[i].contains("_Roo_")) {
					// removes the .class extension
					classes.add(Class.forName(packageName + '.' + files[i].substring(0, files[i].length() - 6)));
				}
			}
		} else {
			throw new ClassNotFoundException(packageName + " is not a valid package");
		}

		return classes;
	}

	/**
	 * Holds the classnames of hibernate dialects for easy reference.
	 */
	private static enum Dialect {
		ORACLE("org.hibernate.dialect.Oracle10gDialect"), MYSQL("org.hibernate.dialect.MySQLDialect"), HSQL(
				"org.hibernate.dialect.HSQLDialect");

		private final String dialectClass;

		private Dialect(String dialectClass) {
			this.dialectClass = dialectClass;
		}

		public String getDialectClass() {
			return dialectClass;
		}
	}

}
