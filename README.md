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

## 3. Diseño
Para forzar un arreglo mas grande de lo que normalmente soportaría un arreglo de java 
(el cual soporta Integer.MAX_VALUE en su capacidad), se decidió utilizar un arreglos 
distribuidos con capacidad Long.MAX_VALUE. Cada PGAS soportara por proceso un espacio de
 tamaño Integer.MAX_VALUE como capacidad máxima, y la sumatoria de todos los tamaños 
 de dichos procesos es igual al tamaño real representado por el arreglo distribuido. 

## 4. Compilación
El proyecto esta construido utilizando Gradle (incorporado en el repositorio).
Descargando el repositorio es suficiente para compilar el proyecto, ya que todas las 
herramientas y scripts se encuentran en el.  

### 4.1. Requisitos
- Java JDK 8 o superior.
- Tener configurada la variable de entorno ***JAVA_HOME***. 

### 4.2. Dependencias
- Se resolverán automáticamente al utilizar alguna actividad de Gradle. 

### 4.3. Instrucciones Recomendadas
Hay 2 scripts `gradlew`, uno para windows y otro para linux. Los siguientes 
parámetros son los recomendados para trabajar con el proyecto, con dichos scripts: 
- `gradlew help`: ayuda sobre las posibles acciones (Tasks) que gradle puede realizar. 
- `gradlew clean`: limpia los directorios del proyecto.   
- `gradlew build`: compila el proyecto.
- `gradlew finalFatJar`: crea un jar con el programa listo para usar.  
- `gradlew junitPlatformTest`:  ejecuta los test de JUnit.
- `gradlew javadoc`:  compila javadoc.

## 5. Uso del Arreglo Distribuido

1. Crear un archivo de configuración en formato JSON (usando UTF-8) 
con la siguiente estructura:
```json
{
    "dataType":"<data type used in toSort (Long or Double supported in current implementation)>",
    "processes": [
        {
            "inetAddress":"<process 1 location>", // ejemplo 192.168.0.5
            "port": <port>, // ejemplo 9000
            "distributedArrays": [
                 {
                  "name": <int name>, //ejemplo 98
                  "toSort": toSort": [<data 1>, <data 2>, <data 3>, etc]  //optional
                 }
            ]
        },
        {
            "inetAddress":"<process 2 location>", 
            "port": <port>,
            "distributedArrays": [
                 {
                  "name": <int name>,
                  "toSort": toSort": [<data 4>, <data 5>, <data 6>, etc] //optional
                 }
            ]
        },
        etc
    ]
}
```
2. Hay dos formas de ejecutar el programa`java -jar ar.edu.unrc......`

## 6. Uso de las simulaciones de prueba con Arreglo Distribuido
Para ejecutar una simulación de varios procesos configurados mediante el archivo "exampleConfig.json", ejecutar:
```
java -cp build/libs/proyecto-final-redes-2017-1.0.0.jar ar.edu.unrc.pellegrini.franco.bubblesort.NetSimulationUsingConfigFile "configFile=exampleConfig.json" -debug 
``` 

## Licencia
[![GNU GPL v3.0](http://www.gnu.org/graphics/gplv3-127x51.png)](http://www.gnu.org/licenses/gpl.html)
