package br.dev.ppaiva.gateway.server.handler.requests;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

public class RequestDispatcher {

	private static final Logger logger = LogManager.getLogger();

	private static RequestDispatcher instance;

	private Set<Method> methods;

	private RequestDispatcher() {
		this.methods = new HashSet<Method>();
	}

	public static synchronized RequestDispatcher getInstance() {
		if (instance == null) {
			instance = new RequestDispatcher();
		}
		return instance;
	}

	public void loadMethods(String packageName) {
		Reflections reflections = new Reflections(
				new ConfigurationBuilder().forPackages(packageName).addScanners(Scanners.MethodsAnnotated));

		this.methods = reflections.getMethodsAnnotatedWith(Path.class);
	}

	public void printMethods() {

		Map<Class<?>, List<Method>> methodsByClass = methods.stream()
				.collect(Collectors.groupingBy(Method::getDeclaringClass));

		System.out.println("Application paths:\n");

		for (Map.Entry<Class<?>, List<Method>> entry : methodsByClass.entrySet()) {
			Class<?> clazz = entry.getKey();
			System.out.println("\t" + clazz.getSimpleName());
			for (Method method : entry.getValue()) {
				Path pathAnnotation = method.getAnnotation(Path.class);

				System.out.printf("\t\t%-6s %s\n", pathAnnotation.method(), pathAnnotation.value());
			}
			System.out.println();
		}
		System.out.println();
	}

	public Object dispatch(String method, String path, String body) throws Exception {
		for (Method m : methods) {
			Path annotation = m.getAnnotation(Path.class);

			path = path.replace("\r", "");
			method = method.replace("\r", "");

			if (annotation.value().equals(path) && annotation.method().toString().equalsIgnoreCase(method)) {
				Class<?>[] parameterTypes = m.getParameterTypes();

				if (parameterTypes.length == 1 && parameterTypes[0] == String.class) {

					Object instance = m.getDeclaringClass().getDeclaredConstructor().newInstance();

					m.setAccessible(true);
					try {
						return m.invoke(instance, body);
					} catch (InvocationTargetException | IllegalAccessException e) {
						logger.error(e);
						e.printStackTrace();
						throw new Exception("error on method dispach " + e.getMessage());
					}
				}
			}
		}

		throw new NoSuchMethodException("Unknown method " + method + " " + path);
	}

	public Set<Method> getMethods() {
		return methods;
	}
}
