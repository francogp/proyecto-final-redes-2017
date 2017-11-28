*Universidad Nacional de Río Cuarto*

**Autor:** Pellegrini Franco Gastón

# Telecomunicaciones y Sistemas Distribuidos - Proyecto final 2017
## 1. Descripción
El objetivo de este proyecto es el desarrollo de una simple estructura 
de datos y primitivas de sincronización que permita implementar el 
modelo de programación Partitioned Global Address Space (PGAS) en una 
arquitectura en red (multicomputadora), ocultando los detalles de 
comunicación entre procesos.

El modelo PGAS presenta al programador un sistema multicomputador, donde 
cada proceso/procesador cuenta con su propia memoria local o privada y 
aporta bloques a una memoria compartida. De esta manera permite al 
programador un estilo de programación de memoria compartida aún en un ambiente de 
memoria distribuida (particionada) donde el acceso a la memoria remota se realiza
por medio de mecanismos de comunicación (mensajes).

A modo de ejemplo, se muestra el siguiente pseudo-código que ordena los elementos 
de un arreglo en forma distribuida (en paralelo):

```
// Set of process 0, 1, ..., N-1
init()                    // initialize system
distributed int[N*1000] a // distribute array on N nodes
bool finish = false       // one copy in each processor
int p = pid()             // local (private) variable

while not finish:
    finish = true
    
    // sort local block
    bubble_sort(a.lowerIndex(p), a.upperIndex(p))
    barrier()
    
    if not Iam(N - 1):
        if a[a.upperIndex(p)] > a[a.lowerIndex(p+1)]:
            swap(a.upperIndex(p), a.lowerIndex(right))
            finish = false  // update local copy
   
    // reduce finish by and, then replicate result
    finish = and_reduce(finish)
```

##### Notas:
* a.lowerIndex(p) obtiene el índice menor del bloque o partición local de a 
en el proceso p.

* a.upperIndex(p) obtiene el índice mayor del bloque o partición local de a 
en el proceso p.
barrier() sincroniza (rendezvous) los procesos.

* and_reduce(v) genera una reducción de los valores de las copias locales de 
v aplicando la operación lógica and. También actúa como un punto de 
sincronización.

Este modelo de programación se conoce como one program multiple data ya 
que es el mismo programa corriendo en paralelo en todos los nodos pero 
operando sobre diferentes datos.
Las primitivas barrier() y reduce se conocen como operaciones de 
comunicación global ya que requieren el intercambio de mensajes entre 
todos los procesos.

Se pide el diseño e implementación de los siguientes componentes:
1. Una estructura de datos de tipo distributed array, que distribuya 
en bloques uniformes entre los procesos. Debería contener operaciones o 
métodos read(i) y write(i,v).
2. Implementar barrier()
3. Implementar and_reduce()

## 2. Consideraciones de diseño
Para una mejor y simple implementación, se debería definir una capa de 
abstracción de manejo de mensajes que corra en el contexto de un thread 
de cada proceso y provea las facilidades de transmisión, recepción y 
entrega de mensajes al proceso.

Esta capa debería proveer operaciones para que un estructura de datos 
distribuida registre los tipos de mensajes recibidos (junto con un método 
o función callback ) para que puedan ser entregados los mensajes de lectura 
y escritura de elementos. En este caso, la capa de gestión de mensajes 
actuaría como un dispatcher de operaciones ante el arribo de mensajes.
Los otros tipos de mensajes, requeridos para implementar las primitivas 
de sincronización y reducción, deberían ser encolados () y entregados bajo 
demanda por el proceso mismo.

Además, esta capa (middleware) debería encapsular el protocolo de transporte 
utilizado.
La figura 1 muestra el esquema de diseño propuesto.

![Figura 1: Arquitectura del sistema](https://i.imgur.com/NLjtvBC.png)

### Referencias
[1] Ajay D. Kshemkalyani, Mukesh Shingal. Distributed Computing. Principles, 
Algorithms and Systems. Cambridge University Press. ISBN-13: 
978-0-511-39341-9. 2008.

[2] [Partitioned Global Address Space.](https://en.wikipedia.org/wiki/Partitioned_global_address_space)

## 3. Implementación
Para forzar un arreglo mas grande de lo que normalmente soportaría un arreglo de java 
(el cual soporta Integer.MAX_VALUE en su capacidad), se decidió utilizar un arreglos 
distribuidos con capacidad Long.MAX_VALUE. Cada PGAS soportara por proceso un espacio de
 tamaño Integer.MAX_VALUE como capacidad máxima, y la sumatoria de todos los tamaños 
 de dichos procesos es igual al tamaño real representado por el arreglo distribuido. 

La organización general de las clases importantes son:
![Figura 2: Diseño](https://i.imgur.com/DGVs8dN.png)

### 3.1. PGAS
Se soportan valores genéricos de `PGAS<I>`. Se provee una implementación 
genérica sobre `DistributedArray<I>` (arreglos distribuidos), y 2 implementaciones para 
soportar los tipos de datos Long y Double. Pueden utilizarse varios PGAS diferentes en un mismo
programa siempre y cuando se les asignen nombres diferentes entre ellos (únicos).

### 3.2. Middleware
Provee una implementación independiente del tipos de datos transportados por los mensajes.
Este utiliza un Listener el cual se encarga de escuchar mensajes en un thread diferente, 
y entrega rápidamente los mensajes a un MessageDispatcher para asi volver a escuchar 
nuevos mensajes. También hace uso de `ProcessesConfigurations` para configurar y administrar los 
procesos y PGAS utilizados.

### 3.3. Message
Los mensajes codifican su contenido en bytes[]. Tienen una cabecera común a todos ellos, 
pero pueden transportar un valor arbitrario al final de los mismos, cuya interpretación se 
deja para el usuario.
 
### 3.3.1 SimpleMessage

Un mensaje esta compuesto por:

![Figura 3: Mensaje](https://i.imgur.com/Himjraz.png)

Donde:
* `PGAS name`: Son 4 bytes que se interpretan como un entero. Este numero es el 
identificador (nombre) del PGAS al cual se dirige el mensaje.
* `Type`: Es 1 byte, el cual se interpreta como una variable de tipo char, y se
instancia en un Enum `MessageType` para representar el tipo del mensaje.
La implementación actual solo soporta mensajes de tipo:
```
public
enum MessageType {
    /**
     * And reduce barrier.
     */
    AND_REDUCE_MSG('A'),
    /**
     * Barrier.
     */
    BARRIER_MSG('B'),
    /**
     * Used to wait this message to continue from a barrier.
     */
    CONTINUE_BARRIER_MSG('C'),
    /**
     * Used to wait this message to continue from an and reduce.
     */
    CONTINUE_AND_REDUCE_MSG('V'),
    /**
     * Close the listener from the middleware.
     */
    END_MSG('E'),
    /**
     * Read a value from the PGAS.
     */
    READ_MSG('R'),
    /**
     * Wait for the requested read value.
     */
    READ_RESPONSE_MSG('S'),
    /**
     * Write a value into a PGAS.
     */
    WRITE_MSG('W'); 
    ...
}
```
* `Index`: Son 8 bytes que se interpretan como una variable de tipo Long para representar
un índice dentro de un Arreglo Distribuido. Nota: el middleware utiliza este campo
para transportar valores de verdad en el and reduce, para simplificar implementaciones.
* `DataSize (int)`: 4 bytes que se interpretan como un entero, y representa la cantidad de bytes 
de bytes que ocupa el dato transportado al final del mensaje. 
* `Data Value`: Cantidad de bytes variable (establecidas por `DataSize (int)`) usado
para transportar información genérica por los mensajes.
 
## 4. Compilación
El proyecto esta construido utilizando Gradle (incorporado en el repositorio).
Descargando el repositorio es suficiente para compilar el proyecto, ya que todas las 
herramientas y scripts se encuentran en el.  

### 4.1. Requisitos
- Java JDK 8 o superior.
- Tener configurada la variable de entorno ***JAVA_HOME*** o en caso contrario GRADLE se 
negará a compilar. 

### 4.2. Dependencias
- Se resolverán automáticamente al utilizar alguna actividad de Gradle. 

### 4.3. Instrucciones Recomendadas
Hay 2 scripts `gradlew`, uno para windows y otro para linux. Los siguientes 
parámetros son los recomendados para trabajar con el proyecto: 
- `gradlew help`: ayuda sobre las posibles acciones (Tasks) que gradle puede realizar. 
- `gradlew clean`: limpia los directorios del proyecto.   
- `gradlew build`: compila el proyecto.
- `gradlew finalFatJar`: crea un jar con el programa listo para usar.  
- `gradlew junitPlatformTest`:  ejecuta los test de JUnit.
- `gradlew javadoc`:  compila javadoc.

## 5. Uso del Arreglo Distribuido para el programa Bubble Sort

### 5.1. Crear un archivo de configuración en formato JSON
 Utilizando UTF-8, utilizar el formato que se describe a continuación mediante un 
 ejemplo de uso:
```json
{
    "dataType":"Long",
    "processes": [
        {
            "inetAddress":"localhost",
            "port": 9000,
            "distributedArrays": [
                 {
                  "name": 15, 
                  "toSort": [1, 6, -8, 4] 
                 }
            ]
        },
        {
            "inetAddress":"192.168.1.100", 
            "port": 8111,
            "distributedArrays": [
                 {
                  "name": 15,
                  "toSort": [3, 1, 10]
                 }
            ]
        }
    ]
}
```
* `dataType`: Establece el tipo de dato que se va a trabajar en el bubble sort. Puede ser `Long` o `Double`.
* `processes`: Lista todos los procesos distribuidos que van a participar del bubble sort. El orden
en que aparecen descritos sera el PID que tendrá cada proceso, comenzando desde el PID 1.
* `inetAddress`: Dirección IP (clase InetAddress) con la ubicación del proceso.
* `port`: Puerto que utiliza el proceso.
* `distributedArrays`: Lista de todos los arreglos distribuidos que se utilizaran en el bubble sort 
(solo uno es usado en estos ejemplos, pero soporta multiples).
* `name`: Nombre identificador de un PGAS, por si se necesita usar varios a la vez.
* `toSort` (opcional): Lista de elementos que el PGAS tiene para trabajar en el proceso actual. Si `toSort`
no esta presente, significa que el parser de configuraciones ignorará los valores de dicho proceso. Esto
nos permite crear archivos de configuración específicos para cada proceso ahorrando memoria en caso de 
existir MUCHOS valores. Solo es necesario colocar valores en el `toSort` correspondiente al proceso que se 
va a ejecutar. 

**Nota**: En el repositorio ya se encuentra un archivo .json de ejemplo para pruebas, llamado `exampleConfig.json`.

### 5.2. Ejecutar procesos involucrados
 Cada proceso debe ser ejecutado de manera independiente. Se debería ejecutar un proceso
 por cada "processes" listado en el archivo de configuración. 
 
 La ejecución de un proceso consta de tres parámetros:
 * `pid`: Identificador del proceso a ejecutar, correspondiente al establecido en el archivo de configuraciones.
 * `configFile`: Dirección del archivo de configuración. Si el path tiene espacios, encerrar todo el parámetro
 entre comillas, por ejemplo `"configFile=/home/carpeta de ejemplo/exampleConfig.json"`.
 * `-debug` (opcional): Si esta presente, se mostrará los mensajes enviados y recibidos por el proceso.
 
 **Nota**: Se recomienda ejecutar el proceso con `pid=1` primero, ya que este es considerado el coordinador.
   
### 5.3. Ejemplo:
 Para ejecuta rel proceso con los datos correspondiente al PID 2 (el segundo de la lista de configuraciones):
```
cd "cloned project directory"
java -cp build/libs/proyecto-final-redes-2017-1.0.0.jar ar.edu.unrc.pellegrini.franco.bubblesort.DistributedBubbleSort pid=2 "configFile=exampleConfig.json" 
```
 
## 6. Uso de simulación de prueba utilizando varios procesos distribuidos (hilos)
Para ejecutar una simulación de varios procesos (utilizando threads) configurados 
mediante un archivo de configuración json (como se definió en 5.1), ejecutar:
```
cd "cloned project directory"
java -cp build/libs/proyecto-final-redes-2017-1.0.0.jar ar.edu.unrc.pellegrini.franco.bubblesort.NetSimulationUsingConfigFile "configFile=exampleConfig.json" -debug 
``` 
* El parámetro `debug`, si está presente, nos deja ver los mensajes enviados y 
recibidos en el calculo del Bubble Sort.
* El parámetro `configFile` utilizara el archivo de configuración "exampleConfig.json"
 ubicado en el directorio raíz del repositorio, para inicializar todos los procesos/hilos
 necesarios para solucionar el problema mediante Bubble Sort Distribuido.

## Licencia
[![GNU GPL v3.0](http://www.gnu.org/graphics/gplv3-127x51.png)](http://www.gnu.org/licenses/gpl.html)
