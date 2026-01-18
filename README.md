# App Rick and Morty - Proyecto Android

## 1. Introducción
Esta app te ayuda a controlar tus episodios de Rick y Morty. Puedes ver la lista completa sacada de una base de datos externa, 
revisar los detalles de cada episodio,personajes incluidos, y marcar los que ya has visto. Además, guarda esa información de los ya vistos la nube.



## 2. Características principales
- Acceso de usuarios: Puedes registrarte e ingresar de forma segura con tu email y clave.
- Lista de capítulos: Verás todos los capítulos de la serie, y los que ya viste tendrán el fondo verde.
- Filtro: Arriba, puedes elegir ver Todos los capítulos o Solo los que ya viste.
- Info del capítulo: Cada capítulo muestra su fecha, código y las fotos de los personajes.
- Guardado en la nube: Cuando marcas un capítulo como visto o no visto, se guarda al instante en Firestore.
- Estadísticas: Hay una pantalla donde ves tu progreso con gráficos y porcentajes.
- Ajustes: Puedes cambiar el idioma y el estilo visual (y se guarda), y también cerrar sesión.
- Acerca de: Aquí encuentras datos del creador de la app y la versión.


## 3. Tecnologías utilizadas
El proyecto se ha hecho en Kotlin, usando Activities y Fragments.
- Para la API: Retrofit 2 + Gson (para obtener los episodios de Rick and Morty).
- Imágenes: Picasso (carga las fotos de los personajes).
- Backend / Nube:
    *   Firebase Authentication (para manejar los usuarios).
    *   Firebase Firestore (base de datos NoSQL para guardar qué capítulos se han visto).
- Interfaz:
    *   RecyclerView (listas rápidas).
    *   CardView y GridLayout (diseño).
    *   LinearLayout con Weights (gráficos personalizados).
- Guardado local: SharedPreferences (para la configuración de la app).


## 4. Instrucciones de uso
Para que este proyecto funcione:
1.  Copia el repositorio con: `git clone https://github.com/nightwolf85/rickymorty.git`.
2.  Abre el proyecto en Android Studio.
3.  Ojo: Necesitas el archivo `google-services.json` de Firebase.
    *   Crea tu propio proyecto en Firebase.
    *   Baja el archivo `google-services.json`.
    *   Pégalo en la carpeta `/app` del proyecto.
4.  Sincroniza el proyecto con Gradle.
5.  Corre el proyecto en un emulador o en tu móvil con acceso a internet.


## 5. Conclusiones del desarrollador
En general, este proyecto ha sido un reto para practicar con fragment y activity. Para mi personalmente me ha parecido un poco complicado ya que además de los
conceptos y tipo de elementos a usar con Android Studio se ha sumado el tener que conectar con una API externa, que no lo había hecho antes, y usar Retrofit o Picasso
para las imágenes y demás. La parte de Firebase también ha sido una novedad ya que no lo conocía y puede tener sus utilidades pero dedicando tiempo a revisar 
bien toda la documentación que ofrece.
Basándome en lo solicitado y lo ofrecido en la teoría, tanto de este tema como del anterior (que también ha sido necesario volver a revisar), se podría actualizar
o completar/añadir algo más de información que pudiera ayudar, porque en ciertos aspectos es bastante limitada y conlleva el buscar información adicional o para
ampliar el temario en fuentes externas.


## 6. Capturas de pantalla
- En el fichero comprimido, además del proyecto, se adjunta vídeo del uso de la aplicación que sirve como captura de la misma.