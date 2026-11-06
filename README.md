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