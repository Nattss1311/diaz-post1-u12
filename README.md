# Sistema de Solicitudes de Reembolso de Gastos Corporativos

## Diseño Propuesto

### RF1 — Validación en varias etapas
* **Punto de variabilidad:** La secuencia, cantidad y reglas de verificación que debe superar una solicitud antes de aprobarse (pudiendo agregarse verificaciones futuras como límite anual sin alterar las existentes).
* **Patrón de diseño:** Chain of Responsibility (Cadena de Responsabilidad).
* **Justificación (Test de explicabilidad):** Desacopla al emisor de la petición de sus receptores permitiendo encadenar validaciones independientes y añadir nuevos eslabones en el futuro sin modificar los existentes ni el servicio.

### RF2 — Cálculo distinto por categoría de gasto
* **Punto de variabilidad:** El algoritmo y fórmula de liquidación del monto aprobado según el tipo de gasto (viaje, representación, capacitación o nuevas categorías).
* **Patrón de diseño:** Strategy (Estrategia).
* **Justificación (Test de explicabilidad):** Encapsula cada fórmula de cálculo bajo una interfaz polimórfica común permitiendo intercambiar y agregar nuevos algoritmos de liquidación en tiempo de ejecución sin modificar el cliente que los ejecuta.

### RF3 — Notificación simultánea a varios interesados
* **Punto de variabilidad:** La cantidad, naturaleza y lógica de reacción de los módulos interesados cada vez que cambia el estado de una solicitud.
* **Patrón de diseño:** Observer (Observador).
* **Justificación (Test de explicabilidad):** Establece una suscripción uno a muchos donde el cambio de estado de una solicitud notifica automáticamente a múltiples interesados desacoplados sin que el servicio conozca quiénes son ni qué acciones ejecutan.

## Decisiones de Diseño

### Decisión de diseño 1: Orden de encadenamiento de validadores
* **Decisión tomada:** Encadenar los validadores en el orden: 1° `ValidadorDocumentosSoporte` → 2° `ValidadorMontoMaximo` → 3° `ValidadorDuplicados`.
* **Justificación técnica:** Se aplica el principio de evaluación de cortocircuito (fail-fast). Las validaciones de documentos soporte y monto máximo se ejecutan en tiempo constante O(1) evaluando atributos en memoria del propio objeto. En contraste, `ValidadorDuplicados` es el eslabón computacionalmente más costoso al requerir iterar sobre una colección O(N) de solicitudes existentes. Al colocar `ValidadorDuplicados` al final de la cadena, si una solicitud carece de documentos soporte o excede el tope de su categoría, el flujo se interrumpe de inmediato sin ejecutar la búsqueda sobre la lista de pendientes, ahorrando ciclos de CPU y memoria. Ubicarlo primero perdería eficiencia al forzar búsquedas innecesarias sobre solicitudes que de todos modos serían rechazadas por datos incompletos.

### Decisión de diseño 2: Mecanismo de selección de estrategias de cálculo
* **Decisión tomada:** Seleccionar la calculadora mediante un mapa indexado por `CategoriaGasto` construido a partir de una lista `List<CalculadoraReembolso>` inyectada automáticamente por Spring con todos los componentes `@Component` registrados, descartando el uso de un `switch` manual.
* **Justificación técnica:** Cumple estrictamente con el principio Abierto/Cerrado (Open/Closed Principle - OCP). Considerando que Finanzas planea incorporar nuevas categorías de gasto en los próximos semestres (por ejemplo, "bienestar"), la inyección automática por lista de Spring permite que únicamente creemos una nueva clase anotada con `@Component` que implemente `CalculadoraReembolso`. Spring la descubrirá, registrará e indexará dinámicamente en el mapa sin tener que tocar, modificar ni recompilar una sola línea de código existente en `SelectorCalculadora`. Por el contrario, un `switch` manual violaría OCP al obligar a modificar el código fuente del selector cada vez que aparezca una nueva categoría.

### Decisión de diseño 3: Capas simples frente a arquitectura Hexagonal completa
* **Decisión tomada:** Mantener la arquitectura en capas simples para la persistencia (el servicio interactúa directamente con `RepositorioSolicitudesJpa` sin interfaz de puerto intermedia), y conservar `ObservadorCambioEstado` como el seam suficiente para la integración externa de Contabilidad.
* **Justificación técnica:** No existe requerimiento ni proyección real de cambiar el motor de base de datos relacional; crear puertos y mappers en persistencia violaría el principio YAGNI (You Ain't Gonna Need It) introduciendo complejidad accidental y sobrecarga cognitiva. Para las notificaciones, el patrón Observer ya aísla completamente al servicio, permitiendo que `NotificadorContabilidad` consuma un API REST externa el próximo trimestre sin tocar el núcleo de la aplicación. (Ver detalle completo en `docs/adr/ADR-001.md`).

### Decisión de diseño 4: Distinción de dependencias de Spring en el paquete procesamiento
* **Decisión tomada:** La Regla 1 de ArchUnit prohíbe expresamente dependencias hacia `org.springframework.data..`, `jakarta.persistence..`, infraestructura y adaptadores web, pero permite las anotaciones de estereotipo de Spring como `@Component`.
* **Justificación técnica:** Anotaciones como `@Component` son únicamente metadatos declarativos de inversión de control (IoC) que facilitan el autodescubrimiento y la inyección por lista de las estrategias y observadores, sin acoplar la lógica de negocio a una tecnología de almacenamiento o protocolo de red particular. Prohibir la totalidad del paquete `org.springframework` forzaría la creación de clases `@Configuration` externas innecesarias agregando sobreingeniería y fricción cognitiva, mientras que aislar selectivamente las clases de JPA y de la capa web garantiza que el procesamiento de negocio permanezca desacoplado de la base de datos y de la infraestructura HTTP.