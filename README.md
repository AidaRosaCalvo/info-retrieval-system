# Sistema de Recuperación de Información

Este proyecto consiste en la construcción de un sistema de recuperación de información que puede manipular documentos de diferentes formatos provenientes de un repositorio de información. La aplicación utiliza herramientas como Lucene y Tika para indexar y extraer información de los documentos. Los resultados de las búsquedas se organizan por grupos de documentos afines utilizando los algoritmos de agrupamiento K-means, Fuzzy C-Means y Linkage.

## Funcionalidades

- **Indexación de documentos**: Utiliza Apache Lucene para indexar documentos de diversos formatos.
- **Extracción de contenido**: Usa Apache Tika para extraer texto y metadatos de los documentos.
- **Búsqueda de información**: Permite realizar búsquedas en el índice creado por Lucene.
- **Agrupamiento de documentos**: Organiza los resultados de las búsquedas en grupos afines utilizando algoritmos de agrupamiento.

## Tecnologías Utilizadas

- **Java**: Lenguaje de programación principal del proyecto.
- **Apache Lucene**: Biblioteca de búsqueda de texto.
- **Apache Tika**: Biblioteca para la detección y extracción de contenido de documentos.
- **K-means, Fuzzy C-Means, Linkage**: Algoritmos de agrupamiento implementados para organizar los resultados.

## Requisitos Previos

- JDK 8 o superior
- Git

## Instalación

1. Clona el repositorio:
    ```sh
    git clone https://github.com/AidaRosaCalvo/info-retrieval-system.git
    cd info-retrieval-system
    ```

## Uso

1. Ejecuta la aplicación.

2. Ingresa los documentos que deseas indexar en el repositorio configurado.

3. Realiza búsquedas y observa cómo se organizan los resultados en grupos afines.


## Contribuciones

Las contribuciones son bienvenidas. Si deseas contribuir, por favor sigue los siguientes pasos:

1. Haz un fork del repositorio.
2. Crea una rama para tu característica (`git checkout -b feature/nueva-caracteristica`).
3. Realiza tus cambios (`git commit -m 'Añadir nueva característica'`).
4. Sube tus cambios a tu fork (`git push origin feature/nueva-caracteristica`).
5. Abre un pull request.
